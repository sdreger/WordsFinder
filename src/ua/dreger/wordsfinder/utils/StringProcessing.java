/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author Sergey
 */
public class StringProcessing {
//---------------------------------------------------------------------------//		
	/**
	 * составление массива кол-ва символов в исходном слове
	 * Пример: слово "кошка"
	 * [а,б,в,г,д,е,ж,з,и,й,к,л,м,н,о,п,р,с,и,у,ф,х,ц,ч,ш,щ,ъ,ы,ь,э,ю,я]
	 * [1,0,0,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0]
	 * @param input слово, количество симолов в которос нужно вычислить
	 * @param printFlag нужно ли печатать результат в stdout?
	 * @return массив, содержащий количество букв заданного слова в алфавитном порядке,
	 * исключая букву ё, но содержи в последнем элементе кол-во дефисов
	 */
	public static int[] charsCount(String input, boolean printFlag){
		Map<Character, Integer> chars = new TreeMap<Character, Integer>();
		for (char letter : input.toCharArray()) {
			chars.put(letter, (chars.containsKey(letter) ? chars.get(letter) + 1 : 1));
		}
		int[] charsCounter = new int[33];
		int currentLetter = 'а';
		for (int i = 0; i < 32; i++) {
			if(chars.containsKey((char)(currentLetter+i))) charsCounter[i] = chars.get((char)(currentLetter+i));
		}
		if(chars.containsKey('-')) charsCounter[32] = chars.get('-');
		if(printFlag) System.out.println(input+" --- "+Arrays.toString(charsCounter));
		return charsCounter;
	}
//---------------------------------------------------------------------------//	
	// http://stackoverflow.com/questions/2000440/php-similar-text-in-java
	// this works the same as php similar_text function as is in php_similar_str, php_similar_char,
	// PHP_FUNCTION(similar_text) in string.c file of php sources

	public static float similarText(String first, String second) {
	    first = first.toLowerCase();
	    second = second.toLowerCase();
	    return (float)(StringProcessing.similar(first, second)*200)/(first.length()+second.length());
	}
//---------------------------------------------------------------------------//	
	private static int similar(String first, String second) { 
	    int p, q, l, sum;
		int pos1=0;
		int pos2=0;
		int max=0;
		char[] arr1 = first.toCharArray();
		char[] arr2 = second.toCharArray();
		int firstLength = arr1.length;
		int secondLength = arr2.length;

		for (p = 0; p < firstLength; p++) {
			for (q = 0; q < secondLength; q++) {
				for (l = 0; (p + l < firstLength) && (q + l < secondLength) && (arr1[p+l] == arr2[q+l]); l++);            
				if (l > max) {
					max = l;
					pos1 = p;
					pos2 = q;
				}
			}
		}
		sum = max;
		if (sum > 0) {
			if (pos1 > 0 && pos2 > 0) {
				sum += StringProcessing.similar(first.substring(0, pos1>firstLength ? firstLength : pos1), second.substring(0, pos2>secondLength ? secondLength : pos2));
			}

			if ((pos1 + max < firstLength) && (pos2 + max < secondLength)) {
				sum += StringProcessing.similar(first.substring(pos1 + max, firstLength), second.substring(pos2 + max, secondLength));
			}
		}       
		return sum;
	}
//---------------------------------------------------------------------------//	
 /**
  * Calculates the similarity (a number within 0 and 1) between two strings.
  * http://ideone.com/oOVWYj
  * http://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
  */
	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2; shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
		/* // If you have StringUtils, you can use it to calculate the edit distance:
		  return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
		  (double) longerLength; */
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
		for (int j = 0; j <= s2.length(); j++) {
			if (i == 0)
				costs[j] = j;
			else {
				if (j > 0) {
					int newValue = costs[j - 1];
					if (s1.charAt(i - 1) != s2.charAt(j - 1))
					newValue = Math.min(Math.min(newValue, lastValue),
					costs[j]) + 1;
					costs[j - 1] = lastValue;
					lastValue = newValue;
				}
			}
		}
		if (i > 0)
			costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
	/**
	 * округляет float до заданношо кол-ва знаков после запятой
	 * @param number число, которое нужно округлить
	 * @param scale кол-во символов после запятой (должно быть строго > 0)
	 * @return округленное число
	 */
	public static float round(float number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        float tmp = number * pow;
        return (float) (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) / pow;
    }
}