package com.ir.cgtool;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

 
public class CodeGeneratorTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CodeGeneratorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CodeGenerator.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testCodeGenerator()
    {
        assertTrue( true );
    }
}
