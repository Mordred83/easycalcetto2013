package edu.easycalcetto.activities;

public class CheckWrapper<T> {
	
	private boolean isChecked;
	private T data ; 
	
	public CheckWrapper(T data, boolean isChecked){
		this.data = data;
		this.isChecked = isChecked;
	}
	
	public CheckWrapper(T data){
		this(data, false);
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public void toggleCheck(){
		this.isChecked = !this.isChecked();
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
}
