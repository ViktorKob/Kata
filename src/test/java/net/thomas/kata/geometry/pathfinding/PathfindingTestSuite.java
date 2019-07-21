package net.thomas.kata.geometry.pathfinding;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.kata.geometry.pathfinding.objects.ObjectTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PathfindingUtilUnitTest.class, ObjectTestSuite.class })
public class PathfindingTestSuite {
}