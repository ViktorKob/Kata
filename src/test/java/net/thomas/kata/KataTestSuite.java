package net.thomas.kata;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.kata.geometry.GeometryTestSuite;
import net.thomas.kata.sorting.SortingTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ GeometryTestSuite.class, SortingTestSuite.class })
public class KataTestSuite {
}