package com.test.targeting.policy;

import com.sabr.exceptions.AssertException;
import com.sabr.targeting.TargetBox;
import com.sabr.targeting.TargetContainer;
import com.sabr.targeting.policies.SideFirstPolicy;
import com.test.NXTAssert;
import com.test.NXTTest;
import com.test.Test;

public class SidePolicyTest extends Test
{
    private SideFirstPolicy policyLeft;
    private SideFirstPolicy policyRight;
    private TargetContainer testContainer;

    private void setUp()
    {
        policyLeft = new SideFirstPolicy(SideFirstPolicy.Side.Left);
        policyRight = new SideFirstPolicy(SideFirstPolicy.Side.Right);
        testContainer = NXTTest.getTestTargetBox();
    }

    private void zeroSampleTest() throws AssertException
    {
        TargetContainer zeroSample = new TargetContainer((byte)0);

        NXTAssert test = new NXTAssert();
        test.assertThat(policyLeft.selectTargetBox(zeroSample), "SidePolicy:zeroSample")
                .isNull();
        test.assertThat(policyRight.selectTargetBox(zeroSample), "SidePolicy:zeroSample")
                .isNull();
    }

    private void singleTargetTest() throws AssertException
    {
        TargetContainer singleTarget = new TargetContainer((byte)1);
        singleTarget.setTarget((byte)0, new TargetBox((short)50, (short)50, (short)50));

        NXTAssert test = new NXTAssert();
        test.assertThat(policyLeft.selectTargetBox(singleTarget), "SidePolicy:singleSample")
                .isEqualTo(singleTarget.getTarget((byte)0));
        test.assertThat(policyRight.selectTargetBox(singleTarget), "SidePolicy:singleSample")
                .isEqualTo(singleTarget.getTarget((byte)0));
    }

    private void multipleTargetsTest() throws AssertException
    {
        NXTAssert test = new NXTAssert();
        test.assertThat(policyLeft.selectTargetBox(testContainer), "SidePolicy:multipleTargets")
                .isNotNull()
                .isEqualTo(testContainer.getTarget((byte)4));

        test.assertThat(policyRight.selectTargetBox(testContainer), "SidePolicy:multipleTargets")
                .isNotNull()
                .isEqualTo(testContainer.getTarget((byte)0));
    }

    @Override
    public void runAllTests() throws AssertException
    {
        setUp();
        zeroSampleTest();
        singleTargetTest();
        multipleTargetsTest();
    }
}