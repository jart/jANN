package de.unikassel.ann.algo;

import org.junit.Test;

import de.unikassel.ann.config.NetConfig;
import de.unikassel.ann.factory.NetworkFactory;
import de.unikassel.ann.model.DataPair;
import de.unikassel.ann.model.DataPairSet;
import de.unikassel.ann.model.Network;
import de.unikassel.ann.model.func.SigmoidFunction;
import de.unikassel.ann.model.func.TanHFunction;
import de.unikassel.ann.strategy.MaxLearnIterationsStrategy;
import de.unikassel.ann.strategy.MinErrorStrategy;
import de.unikassel.ann.strategy.RestartErrorStrategy;
import de.unikassel.ann.strategy.RestartImprovementStrategy;

public class BackPropagationTest {
	

	@Test
	public void testImproveStrategy() {
		NetConfig netConfig = NetworkFactory.createSimpleNet(2, new int[]{2}, 1, true, new SigmoidFunction());
		netConfig.addOrUpdateExisting(new RestartImprovementStrategy(0.00001, 50));
		Network net = netConfig.getNetwork();
		
		DataPairSet trainSet = getXorTrainSet();
		netConfig.getTrainingModule().train(trainSet);
		netConfig.printStats();
		
		
		DataPairSet testSet = getXorTestSet();
		netConfig.getWorkingModule().work(net, testSet);
		System.out.println(testSet);
	}
	
	@Test
	public void testStrategies() {
		NetConfig netConfig = NetworkFactory.createSimpleNet(2, new int[]{2}, 1, true, new SigmoidFunction());
		netConfig.addOrUpdateExisting(new MaxLearnIterationsStrategy(500));
		netConfig.addOrUpdateExisting(new MinErrorStrategy(0.1));
		netConfig.addOrUpdateExisting(new RestartErrorStrategy(0.3, 500));
		Network net = netConfig.getNetwork();
		
		DataPairSet trainSet = getXorTrainSet();
		netConfig.getTrainingModule().train(trainSet);
		netConfig.printStats();
		
		
		DataPairSet testSet = getXorTestSet();
		netConfig.getWorkingModule().work(net, testSet);
		System.out.println(testSet);
	}
	
	@Test
	public void testTanHFunctionWithZeroOne() {
		NetConfig netConfig = NetworkFactory.createSimpleNet(2, new int[]{2}, 1, true, new TanHFunction());
		netConfig.addOrUpdateExisting(new MaxLearnIterationsStrategy(500));
		Network net = netConfig.getNetwork();
		
		// XOR training data
		DataPairSet trainSet = getXorTrainSet();
		// start training
		netConfig.getTrainingModule().train(trainSet);
		// print training results
		netConfig.printStats();
		
		// XOR test / test data
		DataPairSet testSet = getXorTestSet();
		// start working phase
		netConfig.getWorkingModule().work(net, testSet);
		
		// print test data result
		System.out.println(testSet);
	}
	
	@Test
	public void testTanHFunctionWithNegativePositiveOnes() {
		NetConfig netConfig = NetworkFactory.createSimpleNet(2, new int[]{2}, 1, true, new TanHFunction());
		netConfig.addOrUpdateExisting(new MaxLearnIterationsStrategy(500));
		Network net = netConfig.getNetwork();
		
		// XOR training data
		DataPairSet trainSet = new DataPairSet();
		trainSet.addPair(new DataPair(new Double[] {-1.0, -1.0}, new Double[] {-1.0}));
		trainSet.addPair(new DataPair(new Double[] {-1.0, 1.0}, new Double[] {1.0}));
		trainSet.addPair(new DataPair(new Double[] {1.0, -1.0}, new Double[] {1.0}));
		trainSet.addPair(new DataPair(new Double[] {1.0, 1.0}, new Double[] {-1.0}));
		// start training
		netConfig.getTrainingModule().train(trainSet);
		// print training results
		netConfig.printStats();
		
		// XOR test / test data
		DataPairSet testSet = new DataPairSet();
		testSet.addPair(new DataPair(new Double[] {-1.0, -1.0}, new Double[] {Double.NaN}));
		testSet.addPair(new DataPair(new Double[] {-1.0, 1.0}, new Double[] {Double.NaN}));
		testSet.addPair(new DataPair(new Double[] {1.0, -1.0}, new Double[] {Double.NaN}));
		testSet.addPair(new DataPair(new Double[] {1.0, 1.0}, new Double[] {Double.NaN}));
		// start working phase
		netConfig.getWorkingModule().work(net, testSet);
		
		// print test data result
		System.out.println(testSet);
	}
	
	@Test
	public void testForwardAndBackwardPass4XOR() {
		// 2x2x1 topology with bias -> 3x3x1
		NetConfig netConfig = NetworkFactory.createXorNet(500, true);
		Network net = netConfig.getNetwork();
		
		// XOR training data
		DataPairSet trainSet = getXorTrainSet();
		netConfig.getTrainingModule().train(trainSet);
		// print training results
		netConfig.printStats();
		
		// XOR test / test data
		DataPairSet testSet = getXorTestSet();
		// start working phase
		netConfig.getWorkingModule().work(net, testSet);
		
		// print test data result
		System.out.println(testSet);
	}
	
	@Test
	public void testForwardAndBackward4XORwithOutBias() {
		// net without bias (without treshold values) don't perform a good training
		// sometimes the result is realy bad (every result is 0.5)
		
		NetConfig netConfig = NetworkFactory.createXorNet(5000, false);
		Network net = netConfig.getNetwork();
		
		DataPairSet trainSet = getXorTrainSet();
		
		netConfig.getTrainingModule().train(trainSet);
		
		DataPairSet testSet = getXorTestSet();
		netConfig.getWorkingModule().work(net, testSet);
		
		netConfig.printStats();
		System.out.println(testSet);
	}

	/**
	 * Matrix is equals to {@link #testForwardPassWithAlreadyTrainedSynapsesBigMatrix()}<br>
	 * Synapse connections (matrix) looks like this<br>
	 * for connection 0 -> 5 there is no space remaining
	 * <pre>
	 * 0 1 2
	 *  \|X|
	 * 3 4 5
	 *  \|/
	 *   6
	 * </pre>
	 */
	@Test
	public void testForwardPassWithAlreadyTrainedSynapses() {
		// 2x2x1 topology with bias -> 3x3x1
		NetConfig netConfig = NetworkFactory.createSimpleNet(2, new int[] {2}, 1, true, new SigmoidFunction());
		Network net = netConfig.getNetwork();

		//[from neuron][to neuron]
		// this are synapses for a well trained xor net with a 3x3x1 (with bias) topology

		// matrix should be nxn, n=amount of neurons
		Double[][] synapseMatrix = new Double[7][7];
		synapseMatrix[0][4] = 6.145;
		synapseMatrix[0][5] = 1.858;
		synapseMatrix[1][4] = -4.171;
		synapseMatrix[1][5] = -4.930;
		synapseMatrix[2][4] = -4.187;
		synapseMatrix[2][5] = -4.925;
		synapseMatrix[3][6] = -3.078;
		synapseMatrix[4][6] = 6.443;
		synapseMatrix[5][6] = -7.144;
		
		net.getSynapseMatrix().setWeightMatrix(synapseMatrix);
		
		// XOR training data
		DataPairSet testSet = getXorTestSet();
		
		// let work
		netConfig.getWorkingModule().work(net, testSet);
		
		// results should be approx. ok for XOR 
		System.out.println(testSet);
	}
	
	/**
	 * Matrix is equals to {@link #testForwardPassWithAlreadyTrainedSynapses()}<br>
	 * Synapse connections (matrix) looks like this<br>
	 * for connection 00 -> 12,  there is no space remaining
	 * <pre>
	 * 00 01 02
	 *   \| X |
	 * 10 11 12
	 *   \| /
	 *    20
	 * </pre>
	 */
	@Test
	public void testForwardPassWithAlreadyTrainedSynapsesBigMatrix() {
		NetConfig netConfig = NetworkFactory.createSimpleNet(2, new int[] {2}, 1, true, new SigmoidFunction());
		Network net = netConfig.getNetwork();
		
		// 1st and 3rd index = amount of layer
		// 2nd and 4th index = biggest layer size (input == hidden == 3)
		Double[][][][] x = new Double[3][3][3][3];
		
		//[from layer][from neuron][to layer][to neuron]
		// this are synapses for a well trained xor net with a 3x3x1 (with bias) topology
		x[0][0] [1][1] = 6.145;
		x[0][0] [1][2] = 1.858;
		x[0][1] [1][1] = -4.171;
		x[0][1] [1][2] = -4.930;
		x[0][2] [1][1] = -4.187;
		x[0][2] [1][2] = -4.925;
		x[1][0] [2][0] = -3.078;
		x[1][1] [2][0] = 6.443;
		x[1][2] [2][0] = -7.144;
		
		net.getSynapseMatrix().setBigWeightMatrix(x);
		
		DataPairSet testSet = getXorTestSet();
		
		netConfig.getWorkingModule().work(net, testSet);
		
		netConfig.printStats();
		System.out.println(testSet);
	}
	
	public static DataPairSet getXorTrainSet() {
		// XOR training data
		DataPairSet trainSet = new DataPairSet();
		trainSet.addPair(new DataPair(new Double[] {0.0, 0.0}, new Double[] {0.0}));
		trainSet.addPair(new DataPair(new Double[] {0.0, 1.0}, new Double[] {1.0}));
		trainSet.addPair(new DataPair(new Double[] {1.0, 0.0}, new Double[] {1.0}));
		trainSet.addPair(new DataPair(new Double[] {1.0, 1.0}, new Double[] {0.0}));
		return trainSet;
	}
	
	public static DataPairSet getXorTestSet() {
		// XOR test data
		DataPairSet testSet = new DataPairSet();
		testSet.addPair(new DataPair(new Double[] {0.0, 0.0}, new Double[] {Double.NaN}));
		testSet.addPair(new DataPair(new Double[] {0.0, 1.0}, new Double[] {Double.NaN}));
		testSet.addPair(new DataPair(new Double[] {1.0, 0.0}, new Double[] {Double.NaN}));
		testSet.addPair(new DataPair(new Double[] {1.0, 1.0}, new Double[] {Double.NaN}));
		return testSet;
	}

}
