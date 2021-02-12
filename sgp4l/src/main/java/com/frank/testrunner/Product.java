package com.frank.testrunner;

public class Product {

	
	private String name;
	private double sgd;
	private double sgdToPhp;
	private double php;
	private String recommended;
	private double earning;
	
	
	@Override
	public boolean equals(Object object) {

		return object instanceof Product && this.getName().equals( ((Product) object).getName());
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getSgd() {
		return sgd;
	}
	public void setSgd(double sgd) {
		this.sgd = sgd;
	}
	public double getSgdToPhp() {
		return sgdToPhp;
	}
	public void setSgdToPhp(double sgdToPhp) {
		this.sgdToPhp = sgdToPhp;
	}
	public double getPhp() {
		return php;
	}
	public void setPhp(double php) {
		this.php = php;
	}
	public String getRecommended() {
		return recommended;
	}
	public void setRecommended(String recommended) {
		this.recommended = recommended;
	}
	public double getEarning() {
		return earning;
	}
	public void setEarning(double earning) {
		this.earning = earning;
	}

	

	
}
