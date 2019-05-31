package net.thomas.kata.patterns.creational;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ResourceAcquisitionIsInitialization {
	public static void main(String[] args) {
		try (BufferedReader stream = new BufferedReader(new StringReader("Hello, World!"))) {
			System.out.println(stream.readLine());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
