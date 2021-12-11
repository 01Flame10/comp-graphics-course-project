package com.bmstu.cg;

import java.util.ArrayList;
import java.util.List;

public class RayTracing {
    private static int nearestObjectIndex(ArrayList<Float> intersections) {
        int index_of_minimum_value = -1;
        if (intersections.isEmpty()) {
            return -1;
        } else {
            float min = -1;
            for (int i = 0; i < intersections.size(); i++) {
                if (intersections.get(i) > 0.1) {
                    min = intersections.get(i);
                    break;
                }
            }
            if (min > 0) {
                for (int index = 0; index < intersections.size(); index++) {
                    if (intersections.get(index) > 0.1 && intersections.get(index) <= min) { // 0.1 чтобы исключить себя
                        min = intersections.get(index);
                        index_of_minimum_value = index;
                    }
                }

                return index_of_minimum_value;
            } else {
                return -1;
            }
        }
    }


    private static Vector4 refractVector(Vector4 i, Vector4 n, float ior) {
        float eta = 1.0f / ior;
        n = n.negative();
        float cosi = -i.dot3(n);
        if (cosi < 0) {
            cosi *= -1;
            n = n.negative();
            eta = 1.f / eta;
        }
        float k = 1 - eta * eta * (1 - cosi * cosi);
        if (k < 0)
            return null;
        return i.multiply(eta).add(n.multiply(eta * cosi - (float) Math.sqrt(k))).normalized();
    }

    private static Vector4 ReflectVector(Vector4 I, Vector4 N) {
        return I.add(N.multiply(N.dot3(I.negative())).multiply(2)).normalized();
    }


    private static ColorCG getColor(Vector4 intersectionPosition, Vector4 intersectingRayDirection, List<PrimitiveObject> sceneObjects, int indexOfNearestObject, int indexOfFirstObject, List<Source> lightSources, float accuracy, float ambientlight, int recursionDeep) {
        ColorCG nearestObjectColor = sceneObjects.get(indexOfNearestObject).getColor(intersectionPosition);
        Vector4 nearestObjectNormal = sceneObjects.get(indexOfNearestObject).getNormalAt(intersectionPosition).normalized();

        ColorCG finalColor = nearestObjectColor.colorMul(ambientlight);

        if (recursionDeep > 500)
            System.out.println(recursionDeep);

        // тени
        for (int lightIndex = 0; lightIndex < lightSources.size(); lightIndex++) {
            Vector4 lightDirection = lightSources.get(lightIndex).getLightPosition().substitute(intersectionPosition).normalized();

            float cosineAngle = nearestObjectNormal.dot3(lightDirection);

            if (cosineAngle > 0) {
                boolean shadowed = false;

                Vector4 distanceToLight = lightSources.get(lightIndex).getLightPosition().substitute(intersectionPosition);
                float distanceToLightMagnitude = distanceToLight.length3();
                Vector4 distanceToLightNorm = distanceToLight.normalized();
                Ray shadowRay = new Ray(intersectionPosition, distanceToLightNorm);
                List<Float> secondaryIntersections = new ArrayList<>();

                for (int objectIndex = 0; objectIndex < sceneObjects.size() && !shadowed; objectIndex++) {
                    secondaryIntersections.add(sceneObjects.get(objectIndex).findIntersection(shadowRay));
                }

                for (int c = 0; c < secondaryIntersections.size(); c++) {
                    if (secondaryIntersections.get(c) > 0.01f) {
                        if (secondaryIntersections.get(c) <= distanceToLightMagnitude) {

                            shadowed = true;
                        }
                        break;
                    }
                }
                if (!shadowed) {
                    float intensiveCos = cosineAngle * lightSources.get(lightIndex).getLightIntensive() / (nearestObjectNormal.length3() * lightDirection.length3());
                    finalColor = finalColor.colorAdd(nearestObjectColor.colorMul(lightSources.get(lightIndex).getLightColor()).colorMul(intensiveCos));
                    // Находим отраженный луч 
                    if (nearestObjectColor.specular > 0) {
                        Vector4 scalar1 = nearestObjectNormal.multiply(cosineAngle).multiply(2);
                        Vector4 reflectionDirection = scalar1.substitute(lightDirection).normalized();
                        float specular = reflectionDirection.dot3(intersectingRayDirection.negative());
                        if (specular > 0.1) {
                            float specular_object = nearestObjectColor.specular * 100;
                            specular = (float) Math.pow(specular, specular_object);
                            finalColor = finalColor.colorAdd(lightSources.get(lightIndex).getLightColor().colorMul(0.5f * specular * lightSources.get(lightIndex).getLightIntensive()));
                        }

                    }
                }
            }
        }

        // отражение
        if (nearestObjectColor.getSpecial() > 0 && nearestObjectColor.getSpecial() <= 1) {
            Vector4 reflectionDirection = ReflectVector(intersectingRayDirection, nearestObjectNormal);
            Ray reflection_ray = new Ray(intersectionPosition, reflectionDirection);
            ArrayList<Float> reflectionIntersections = new ArrayList<>();

            for (int reflection_index = 0; reflection_index < sceneObjects.size(); reflection_index++) {
                reflectionIntersections.add(sceneObjects.get(reflection_index).findIntersection(reflection_ray));
            }


            int indexOfNearestObjectWithReflection = nearestObjectIndex(reflectionIntersections);
            if (indexOfNearestObjectWithReflection != -1 && recursionDeep < 10) {
                if (reflectionIntersections.get(indexOfNearestObjectWithReflection) > 0.01) {
                    Vector4 reflectionIntersectionPosition = intersectionPosition.add(reflectionDirection.multiply(reflectionIntersections.get(indexOfNearestObjectWithReflection)));
                    Vector4 reflectionIntersectionRayDirection = reflectionDirection;

                    ColorCG reflectionIntersectionColor = getColor(reflectionIntersectionPosition, reflectionIntersectionRayDirection, sceneObjects, indexOfNearestObjectWithReflection, indexOfNearestObjectWithReflection, lightSources, 0.0001f, ambientlight, ++recursionDeep);

                    finalColor = finalColor.colorMul(1 - finalColor.getSpecial()).colorAdd(reflectionIntersectionColor.colorMul(nearestObjectColor.getSpecial()));
                }
            }
        }


        // преломление
        if (nearestObjectColor.getReflectionCoefficient() > 0) {
            Vector4 refractionDirection = refractVector(intersectingRayDirection, nearestObjectNormal, nearestObjectColor.getReflectionCoefficient());
            if (refractionDirection != null) {

                Ray refraction_ray = new Ray(intersectionPosition, refractionDirection);
                ArrayList<Float> refractionIntersections = new ArrayList<>();


                for (int refractionIndex = 0; refractionIndex < sceneObjects.size(); refractionIndex++) {
                    if (refractionIndex != indexOfFirstObject) {
                        refractionIntersections.add(sceneObjects.get(refractionIndex).findIntersection(refraction_ray));

                    } else {
                        refractionIntersections.add(-1.f);
                    }

                }
                int indexOfNearestObjectWithRefraction = nearestObjectIndex(refractionIntersections);
                if (indexOfNearestObjectWithRefraction != -1 && recursionDeep < 10) {
                    if (refractionIntersections.get(indexOfNearestObjectWithRefraction) > 0.1) {

                        Vector4 refractionIntersectionPosition = intersectionPosition.add(refractionDirection.multiply(refractionIntersections.get(indexOfNearestObjectWithRefraction)));
                        Vector4 refractionIntersectionRayDirection = refractionDirection;

                        ColorCG refractionIntersectionColor = getColor(refractionIntersectionPosition, refractionIntersectionRayDirection, sceneObjects, indexOfNearestObjectWithRefraction, indexOfNearestObjectWithRefraction, lightSources, 0.0001f, ambientlight, ++recursionDeep);

                        finalColor = finalColor.colorMul(1 - nearestObjectColor.opacity).colorAdd(refractionIntersectionColor.colorMul(nearestObjectColor.opacity));
                    }
                }
            }

        }
        return finalColor.limit();
    }


    private static float[] normCoords(int x, int y, float increment, int width, int height, float aspectratio) {
        float xamnt, yamnt;
        //float tanHalfFOV = (float)Math.tan(70.f / 2);
        //return new float[] {x, y};
        if (width > height) {
            xamnt = ((x + increment) / width) * aspectratio - (((width - height) / (float) height) / 2);
            yamnt = (y + increment) / height;
        } else if (height > width) {
            // the imager is taller than it is wide
            xamnt = (x + increment) / width;
            yamnt = ((y + increment) / height) / aspectratio - (((height - width) / (float) width) / 2);
        } else {
            // the image is square
            xamnt = (x + increment) / width;
            yamnt = ((y) + increment) / height;
        }

        return new float[]{xamnt, yamnt};
    }

    public void renderRayTracing(RenderSceneTriangle target, int width, int height,
                                 Camera camera, List<PrimitiveObject> sceneObjects, List<Source> lightSources,
                                 float ambientlight, int aadepth) {

        int threadNumber = 16;
        Thread[] threads = new Thread[threadNumber];

        int numOfParts = width / threadNumber;
        int numOfPartsRemainder = width % threadNumber;

        for (int i = 0; i < threadNumber - 1; ++i) {
            int begin = i * numOfParts;
            int end = begin + numOfParts;
            threads[i] = new Thread(new TracingThread(target, width, height, camera, sceneObjects, lightSources, ambientlight, aadepth, begin, end));
            threads[i].start();
        }
        int begin = (threadNumber - 1) * numOfParts;
        int end = begin + numOfParts + numOfPartsRemainder;
        threads[threadNumber - 1] = new Thread(new TracingThread(target, width, height, camera, sceneObjects, lightSources, ambientlight, aadepth, begin, end));
        threads[threadNumber - 1].start();
        //long start = System.nanoTime();

        for (int i = 0; i < threadNumber; ++i) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //long finish = System.nanoTime();
        //System.out.println("Время работы = " + (finish - start)); //767174622    935248932  329834388
        // с - 851408253  без - 3022528243
    }

    class TracingThread implements Runnable {
        RenderSceneTriangle target;
        int width;
        int height;
        Camera camera;
        List<PrimitiveObject> sceneObjects;
        List<Source> lightSources;
        float ambientlight;
        int aliasingKoef, begin, end;

        public TracingThread(RenderSceneTriangle target_v, int width_v, int height_v,
                             Camera camera_v, List<PrimitiveObject> sceneObjects_v, List<Source> lightSources_v,
                             float ambientlight_v, int aadepth_v, int begin_v, int end_v) {
            target = target_v;
            width = width_v;
            height = height_v;
            camera = camera_v;
            sceneObjects = sceneObjects_v;
            lightSources = lightSources_v;
            ambientlight = ambientlight_v;
            aliasingKoef = aadepth_v;
            begin = begin_v;
            end = end_v;
        }

        public void run() {
            Vector4 campos = camera.getCameraPosition();
            Vector4 camdir = camera.getCameraDirection();
            Vector4 camright = camera.getCameraRight();
            Vector4 camdown = camera.getCameraDown();

            float aspectratio = (float) width / (float) height;
            int aliasingIndex;
            float xamnt, yamnt;
            float accuracy = 0.001f;

            for (int x = begin; x < end; x++) {
                for (int y = 0; y < height; y++) {
                    float[] tempRed = new float[aliasingKoef * aliasingKoef];
                    float[] tempGreen = new float[aliasingKoef * aliasingKoef];
                    float[] tempBlue = new float[aliasingKoef * aliasingKoef];

                    for (int aax = 0; aax < aliasingKoef; aax++) {
                        //for (int aay = 0; aay < aliasing_koef; aay++) {
                        int aay = 0;
                        aliasingIndex = aay * aliasingKoef + aax;

                        if (aliasingKoef == 1) {
                            float[] result = normCoords(x, y, 0.5f, width, height, aspectratio);
                            xamnt = result[0];
                            yamnt = result[1];
                        } else {
                            float[] result = normCoords(x, y, (float) aax / ((float) aliasingKoef), width, height, aspectratio);
                            xamnt = result[0];
                            yamnt = result[1];
                        }
                        Vector4 camRayOrigin = campos;
                        Vector4 camRayDirection = camdir.add(camright.multiply(xamnt - 0.5f).add(camdown.multiply(yamnt - 0.5f))).normalized();
                        Ray camRay = new Ray(camRayOrigin, camRayDirection);
                        ArrayList<Float> intersections = new ArrayList<>();
                        for (PrimitiveObject sceneObject : sceneObjects) {
                            intersections.add(sceneObject.findIntersection(camRay));
                        }
                        int indexOfNearestObject = nearestObjectIndex(intersections);
                        if (indexOfNearestObject == -1) {
                            tempRed[aliasingIndex] = 0;
                            tempGreen[aliasingIndex] = 0;
                            tempBlue[aliasingIndex] = 0;
                        } else {
                            if (intersections.get(indexOfNearestObject) > 0.00001) {

                                Vector4 intersectionPosition = camRayOrigin.add(camRayDirection.multiply(intersections.get(indexOfNearestObject)));
                                int recurDeep = 0;

                                ColorCG intersectionColor = getColor(intersectionPosition, camRayDirection, sceneObjects, indexOfNearestObject, indexOfNearestObject, lightSources, accuracy, ambientlight, recurDeep);

                                tempRed[aliasingIndex] = intersectionColor.getRed();
                                tempGreen[aliasingIndex] = intersectionColor.getGreen();
                                tempBlue[aliasingIndex] = intersectionColor.getBlue();
                            }
                        }
                        //}
                    }
                    float totalRed = 0;
                    float totalGreen = 0;
                    float totalBlue = 0;

                    for (int iRed = 0; iRed < aliasingKoef; iRed++) {
                        totalRed = totalRed + tempRed[iRed];
                    }
                    for (int iGreen = 0; iGreen < aliasingKoef; iGreen++) {
                        totalGreen = totalGreen + tempGreen[iGreen];
                    }
                    for (int iBlue = 0; iBlue < aliasingKoef; iBlue++) {
                        totalBlue = totalBlue + tempBlue[iBlue];
                    }

                    double avgRed = totalRed / (aliasingKoef);
                    double avgGreen = totalGreen / (aliasingKoef);
                    double avgBlue = totalBlue / (aliasingKoef);

                    target.drawPixel(x, y, (byte) 0xFF,
                            (byte) ((int) (avgBlue * 255) & 0xFF),
                            (byte) ((int) (avgGreen * 255) & 0xFF),
                            (byte) ((int) (avgRed * 255) & 0xFF));
                }
            }
        }
    }
}


