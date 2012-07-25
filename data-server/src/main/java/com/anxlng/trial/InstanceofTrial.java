/**
 * data-server. 2012-6-27
 */
package com.anxlng.trial;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
interface Animal {

	void say();
}


class Pig implements Animal{

	public void say() {
		System.out.println("哼哼");
	}
}

class Dog implements Animal{

	public void say() {
		System.out.println("汪汪");
	}
}

public class InstanceofTrial {
	
	public void home(Animal animal) {
		System.out.println("goto animal home");
		animal.say();
	}
	
	public  void home(Pig pig) {
		System.out.println("goto pig home");
		pig.say();
	}
	
	public void home(Dog dog) {
		System.out.println("goto dog home");
		dog.say();
	}
	
	public static void main(String[] args) {
		InstanceofTrial m = new InstanceofTrial();
		m.home(new Pig());
		m.home(new Dog());
		Animal a = new Dog();
		m.home(a);
	}
}