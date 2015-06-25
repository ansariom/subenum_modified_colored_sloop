package org.tmu.subenum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
	public static String getAdjMtxOfSubgraph(long l, int motifSize, long values, int subgNo) {
		String outMtx = "";
		byte[] arr = ByteArray.longToByteArray(l, motifSize * motifSize * 2);
//		System.out.println(Arrays.toString(arr));
		
		for (int i = 0; i < motifSize; i++) {
			if (i == 0)
				outMtx = subgNo + ",";
			for (int j = 0; j < motifSize; j++) {
				outMtx = outMtx + arr[(i * motifSize) + j];
			}
			if (i == 0)
				outMtx = outMtx + ", " + values + "\n";
			else
				outMtx += "\n";
		}
		
    	return outMtx += "\n";
	}
	
	public static String getCanonicalLabeling(long longValueOfSubg, int motifSize) throws IOException {
		char[] matrix = getAdjMtxOfSubgraph(longValueOfSubg, motifSize, 2, 1).toCharArray();
		int[] lab = new int[motifSize];
		int[] permutationList = generatePermutationList(motifSize);
		
		int l = 3;
		int a = 0, b = 0, k = 0;
		char[] max = new char[l * l];
		for (int i = 0; i < l * l; i++) {
			max[i] = '0';
		}
		for (int count = 0; count < permutationList.length / l; count++) {
			boolean compare = true;
			int turn = 0;
			int nn = 0;
			int cc = count * l;
			for (int i = 0; i < l; i++) {
				a = permutationList[cc + i];
				nn = a * l;
				for (int j = 0; j < l; j++) {
					b = permutationList[cc + j];
					k = nn + b ;
					if (i == j)
						continue;
					char c = matrix[k];
					int index = (i * l) + j;
					if (compare) {
						if (max[index] < c) {
							turn = 1;
							compare = false;
							max[index] = c;
						} else if (max[index] > c)
							compare = false;
					} else if (turn == 1) {
						max[index] = c;
					}
				}
			}
			if (turn == 1) {
				for (int i = 0; i < l; i++) {
					lab[i] = permutationList[cc + i];
				}
			}
		}
		char[] tempMtx = new char[motifSize* motifSize];
		char[] temp2Mtx = new char[motifSize * motifSize];
		for (int i = 0; i < lab.length; i++) {
			for (int j = 0; j < lab.length; j++) {
				// exchange rows
				tempMtx[(i * motifSize) + j] = matrix[(lab[i] * motifSize) + j];
			}
		}
		for (int i = 0; i < lab.length; i++) {
			for (int j = 0; j < lab.length; j++) {
				// exchange rows
				temp2Mtx[(j * motifSize) + i] = tempMtx[(j * motifSize) + lab[i]];
			}
		}
		
		return Arrays.toString(temp2Mtx);
	}
	
	public static int[] generatePermutationList(int motifSize) {
		List<Integer> permList = new ArrayList<Integer>();
		String permString = "";
		for (int i = 0; i < motifSize; i++) {
			permString += i;
		}
		permutation(permString, permList);
		int[] permutationList = new int[permList.size()];
		for (int i = 0; i < permList.size() / motifSize; i++) {
			for (int j = 0; j < motifSize; j++) {
				int index = i * motifSize + j;
				permutationList[index] = (permList.get(index));
			}
		}
		return permutationList;

	}
	public static void permutation(String str, List<Integer> permList) {
		permutation("", str, permList);
	}

	private static void permutation(String prefix, String str,
			List<Integer> permList) {
		int n = str.length();
		if (n == 0) {
			for (int i = 0; i < prefix.length(); i++) {
				permList.add(Integer.valueOf(prefix.substring(i, i + 1)));
			}
		} else {
			for (int i = 0; i < n; i++)
				permutation(prefix + str.charAt(i),
						str.substring(0, i) + str.substring(i + 1, n), permList);
		}
	}
}
