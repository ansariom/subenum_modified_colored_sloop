package org.tmu.subenum;

import java.util.Arrays;

public class TestMitra {
	public static void main(String[] args) {
//		System.out.println(Arrays.toString(longToByteArray(36, 10)));
//		System.out.println(byteArrayToLong(longToByteArray(167, 10)));
		byte[] inArr = new byte[] {2, 0, 1, 0, 1, 1, 1, 0, 0};
		System.out.println(byteArrayToLong(inArr));

		System.out.println(Arrays.toString(longToByteArray(135504, 18)));
		
	}
	
    public static byte[] longToByteArray(long a, int size) {
    	byte[] temparr = new byte[size];
        byte[] arr = new byte[size/2];
        for (int i = 0; i < size; i++) {
        	if ((a & (1L << i)) != 0)
    			temparr[size - i -1] = 1;
        }
        for (int i = 0; i < size; i = i + 2) {
        	arr[i/2] = (byte) (temparr[i + 1] + (temparr[i] * 2)); 
			
		}
        System.out.println(Arrays.toString(arr));
        return arr;
    }

    public static long byteArrayToLong(byte[] arr) {
        if (arr.length > 64)
            throw new IllegalStateException("Array size is larger than 64: " + arr.length);

        long result = 0;
//        for (int k = 0; k < arr.length; k++) {
//        	for (int i = 0; i < 2; i++)
//        		if ((arr[k] & (1 << i)) != 0) {
////                	arr[i] = 1;
//        			result |= (1L << ((k * 2) + i));                	
//        		}
//        }
//        
        int s = arr.length;
        for (int k = 0; k < s; k++) {
        	for (int i = 0; i < 2; i++)
                if ((arr[s-k-1] & (1 << i)) != 0) {
//                	arr[i] = 1;
                	result |= (1L << ((k * 2) + i));                	
                }
		}
        
        return result;
    }
}
