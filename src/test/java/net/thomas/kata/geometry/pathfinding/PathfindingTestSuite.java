package net.thomas.kata.geometry.pathfinding;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.kata.geometry.pathfinding.objects.PortalUnitTest;
import net.thomas.kata.geometry.pathfinding.objects.TriangleUnitTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PathfindingUtilUnitTest.class, PortalUnitTest.class, TriangleUnitTest.class })
public class PathfindingTestSuite {
}