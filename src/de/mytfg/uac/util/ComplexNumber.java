/*
 * I try to make the calculation as simple as possible.
 * I start with a complex number in algebraic form, e.g. z= a + i*b,
 * while a is called the real part, and b the imaginary part of the number. 
 * In the following programm there is no aspect of the imaginary number i, but only its prefactor.
 */
package de.mytfg.uac.util;

/**
 * @author onschti
 *
 */
public class ComplexNumber {

	private double real;
	private double ima;

	/**
	 * The parameter real represents the real part of the complex number,
	 * parameter ima the imaginary prefactor.
	 * 
	 * @param real
	 * @param ima
	 */
	public ComplexNumber(){
	    this.real = 0;
        this.ima = 0;
	}
	
	public ComplexNumber(double real, double ima) {
		this.real = real;
		this.ima = ima;
	}

	public ComplexNumber(boolean unwichtig, double ima) {
		this.ima = ima;
		this.real = 1;
	}

	public ComplexNumber(double real, boolean unwichtig) {
		this.real = real;
		this.real = 1;
	}

	/*
	 * Summation by calculating the real and imaginary number apart. The new
	 * number is saved as the executive object.
	 */
	public ComplexNumber cSum(ComplexNumber b) {
		double bReal = b.getReal();
		double bIma = b.getIma();
		double cReal = this.real + bReal;
		double cIma = this.ima + bIma;
		return new ComplexNumber(cReal, cIma);
	}
	
	/*
	 * Subtraction by calculating the real and imaginary number apart. The new
	 * number is saved as the executive object.
	 */
	public ComplexNumber cSub(ComplexNumber b) {
		double bReal = b.getReal();
		double bIma = b.getIma();
		double cReal = this.real - bReal;
		double cIma = this.ima - bIma;
		return new ComplexNumber(cReal, cIma);
	}

	/*
	 * Multiplying by calculating the real and imaginary number apart. The new
	 * number is saved as the executive object.
	 */
	public ComplexNumber cMul(ComplexNumber b) {
		double bReal = b.getReal();
		double bIma = b.getIma();
		double cReal = (this.real * bReal) - (this.ima * bIma);
		double cIma = (this.real * bIma) + (this.ima * bReal);
		return new ComplexNumber(cReal, cIma);
	}

	/*
	 * Division by calculating the real and imaginary number apart. The new
	 * number is saved as the executive object.
	 */
	public ComplexNumber cDiv(ComplexNumber b) {
		double bReal = b.getReal();
		double bIma = b.getIma();
		double cReal = ((this.real * bReal) + (this.ima * bIma))
				/ ((bReal * bReal) + (bIma * bIma));
		double cIma = ((bReal * this.ima) - (this.real * bIma))
				/ ((bReal * bReal) + (bIma * bIma));
		return new ComplexNumber(cReal, cIma);
	}

	/*
	 * For exponentiating, we use the cPro method (complex product) again. We
	 * execute the method on the object we want to exponentiate and tell the
	 * exponent as int.
	 */
	public ComplexNumber cExp(int exponent) {
		ComplexNumber c = this.clone();
		for (int i = 1; i < exponent; i++)
			c = c.cMul(this);
		return c;
	}

	public double getReal() {
		return real;
	}

	public void setReal(double real) {
		this.real = real;
	}

	public double getIma() {
		return ima;
	}

	public void setIma(double ima) {
		this.ima = ima;
	}
	
	@Override
	public String toString() {
		return "ComplexNumber " + real + " + " + ima + " * i";
	}

	@Override
	public ComplexNumber clone() {
		return new ComplexNumber(real, ima);
	}
}