package net.thomas.kata.geometry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.kata.geometry.algorithms.AlgorithmTestSuite;
import net.thomas.kata.geometry.objects.ObjectTestSuite;
import net.thomas.kata.geometry.pathfinding.PathfindingTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AlgorithmTestSuite.class, ObjectTestSuite.class, PathfindingTestSuite.class })
public class GeometryTestSuite {
}