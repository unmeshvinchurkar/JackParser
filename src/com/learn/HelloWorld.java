package com.learn;

class Person {

	String name;
	int age;
	
	static String hairColor;

}

public class HelloWorld {

	public static void main(String args[]) {
		
		Person.hairColor="black";

		Person girija = new Person();
		girija.name = "Girija";
		girija.age = 33;

		Person unmesh = new Person();
		unmesh.name = "Unmesh";
		unmesh.age = 33;
		
		
		System.out.println("Girija hair color "+ girija.hairColor);
		System.out.println("unmesh hair color "+ unmesh.hairColor);
		
		
		unmesh.hairColor="white";
		
		System.out.println("Girija hair color "+ girija.hairColor);
		System.out.println("unmesh hair color "+ unmesh.hairColor);
		
		if(girija.age < unmesh.age){
			System.out.println("Girija is younger ");
		}
		
		if(girija.age == unmesh.age){
			System.out.println("same age");
		}
		
		if(girija.age > unmesh.age){
			System.out.println("Girija is older ");
		}

	}

}
