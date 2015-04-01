/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ua.dreger.wordsfinder.data;

import java.io.Serializable;
import ua.dreger.wordsfinder.utils.StringProcessing;
import java.util.Objects;

/**
 * Класс, инкапсулирующий в себе: слово, часть речи и процент схожести с базовым словом
 * @author Sergey
 */
public class Word implements Comparable<Word>, Serializable{
	private String word;
	private String type;
	private float similarity;

	public String getWord() {
		return word;
	}

	public String getType() {
		return type;
	}

	public float getSimilarity() {
		return similarity;
	}

	public Word(String word, String type, String compare) {
		this.word = word;
		this.type = type;
		similarity = StringProcessing.similarText(compare, word);
		similarity = StringProcessing.round(similarity, 1);
	}

	@Override
	public String toString() {
		return "Word: "+word+"; Type: "+type+"; Similarity: "+similarity;
	}

	@Override
	public int compareTo(Word o) {
		float compare = this.similarity - o.getSimilarity();
		if(compare == 0) return 0;
		//for reverce order
		else return (compare > 0) ? -1 : 1;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.word);
		hash = 97 * hash + Objects.hashCode(this.type);
		hash = 97 * hash + Float.floatToIntBits(this.similarity);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Word other = (Word) obj;
		if (!Objects.equals(this.word, other.word)) {
			return false;
		}
		if (!Objects.equals(this.type, other.type)) {
			return false;
		}
		if (Float.floatToIntBits(this.similarity) != Float.floatToIntBits(other.similarity)) {
			return false;
		}
		return true;
	}
}
