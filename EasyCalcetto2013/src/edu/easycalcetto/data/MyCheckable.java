package edu.easycalcetto.data;

public interface MyCheckable<T> {
	T getData();
	boolean isChecked();
	void setChecked(boolean value);
	void toggle();
}
