package net.thomas.kata.ugp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.kata.ugp.engine.TickingEngineUnitTest;
import net.thomas.kata.ugp.world.action.ActionAcceptanceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TickingEngineUnitTest.class, ActionAcceptanceTest.class })
public class UgpTestSuite {
}