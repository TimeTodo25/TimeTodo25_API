package com.pape.timetodo.global.util;

import com.pape.timetodo.global.exception.AppException;
import com.pape.timetodo.global.exception.ExceptionCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    
    private static Random random = new Random(); 

    /**
     * maxCount 안에서 size 크기만큼 랜덤 정수 배열을 가져옵니다[중복 X].
     * @param maxCount
     * @param size
     * @return
     */
    public static Integer[] randomIntegerArray(int maxCount , int size) {

        if(maxCount < size) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);

        Integer[] result = new Integer[size]; 

        HashSet<Integer> notDuplication =  new HashSet<>();

        for (int i = 0; i < size;) {
            int node = random.nextInt(maxCount);

            if (notDuplication.add(node)) {                
                result[i] = node;
                i++;
            }
        }

        return result;
    }

    /**
     * maxCount 안에서 size 크기만큼 랜덤 정수 배열을 가져옵니다[중복 O].
     * @param maxCount
     * @param size
     * @return
     */
    public static Integer[] randomAllowDuplicationIntegerArray(int maxCount , int size) {

        Integer[] result = new Integer[size]; 

        for (int i = 0; i < size; i++) {
            result[i] = random.nextInt(maxCount);
        }

        return result;
    }

//    /**
//     * 배열 인덱스를 무작위로 가져옵니다. [중복X]
//     * @param <P>
//     * @param array
//     * @param size
//     * @return
//     */
//    public static <P> P[] randomArray(P[] array, int size) {
//        int max = array.length;
//
//        if(max < size) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);
//
//        Integer[] indexArray = RandomUtil.randomIntegerArray(max, size);
//
//        Class<?> componentType = array.getClass().getComponentType();
//        P[] result = (P[]) Array.newInstance(componentType, size);
//
//        for (int i = 0; i < size; i++) {
//            result[i] = array[indexArray[i]];
//        }
//
//        return result;
//    }

    /**
     * 리스트 인덱스를 무작위로 가져옵니다. [중복X]
     * @param <P>
     * @param array
     * @param size
     * @return
     */
    public static <P> List<P> randomArray(List<P> array, int size) {
        int max = array.size();

        if(max < size) throw new AppException(ExceptionCode.NON_VALID_PARAMETER);

        Integer[] indexArray = RandomUtil.randomIntegerArray(max, size);        
        List<P> result = new ArrayList<>();
        
        for (int i = 0; i < size; i++) {                        
            result.add(array.get(indexArray[i]));
        }

        return result;
    }

}   
