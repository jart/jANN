package de.unikassel.ann.model;

import java.util.List;
import java.util.Random;

import de.unikassel.ann.model.func.SigmoidFunction;
import de.unikassel.mdda.MDDAPseudo;
import de.unikassel.threeD.Board3D;

public class SomNetwork extends BasicNetwork {
	
	public static long WAIT = 20; 
	
	private int[] dimension;
	
	private Layer inputLayer;
	
	private MDDAPseudo<Neuron> neuronArrayWrapper;
	
	private Layer outputLayer;
	
//	SynapseMatrix synapseMatrix;

	private int neuronIdCounter;

private int inputLayerSize;

private Board3D listener;
	
	public SomNetwork(int inputSize, int... outputDimension) {
		super();
//		if (inputSize != outputDimension.length) {
//			throw new IllegalArgumentException("input size != dimension size");
//		}
		neuronIdCounter = 0;
		inputLayerSize = inputSize;
		dimension = outputDimension;
		inputLayer = new Layer();
		addLayer(inputLayer);
		outputLayer = new Layer();
		addLayer(outputLayer);
		
		for (int i=0; i<inputSize; i++) {
			Neuron neuron = new Neuron(new SigmoidFunction(), false);
			neuron.setId(neuronIdCounter++);
			inputLayer.addNeuron(neuron);
		}
		
		
		
		neuronArrayWrapper = new MDDAPseudo<Neuron>(outputDimension);
		Object[] multiDimArray = (Object[]) neuronArrayWrapper.getArray();
//		synapseMatrix = new SynapseMatrix(this, inputSize, multiDimArray.length+inputSize);
		for (int i=0; i<multiDimArray.length; i++) {
			Neuron n = new Neuron(new SigmoidFunction(), false);
			n.setId(neuronIdCounter++);
			outputLayer.addNeuron(n);
			multiDimArray[i] = n;
			for (Neuron fromNeuron : inputLayer.getNeurons()) {
				Synapse s = new Synapse(fromNeuron, n);
//				synapseMatrix.addOrUpdateSynapse(s);
			}
		}
	}
	
	public void train() {
		trainStep1();
		trainStep2();
	}
	
	public MDDAPseudo<Neuron> getMultiArray() {
		return neuronArrayWrapper;
	}
	
	private void trainStep1() {
		double factorDecrementor = 0.0032; // = 0.0032
		int neighborRadius = 6;
		double factor = 0.9D;

		for (int i = 0; i < 250; i++) {
//			neighborRadius = 6;
			for (int k = 0; k < 50; k++) {
				double[] inputVector = createRandomVector(-1, 1);
				run(inputVector, factor, neighborRadius);
			}

			factor -= factorDecrementor;
			
			neighborRadius--;

//			paramKohonenPanel.setPhaseInfo(1, i);
//			paramKohonenPanel.repaint();
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void trainStep2() {
		double factor = 0.1; 
		double factorDecrementor = 0.08; // 0.08

		for (int i = 0; i < 100; i++) {
			for (int k = 0; k < 75; k++) {
				double[] inputVector = createRandomVector(-1, 1);
				run(inputVector, factor, 1);
				
			}

			factor -= factorDecrementor;

//			paramKohonenPanel.setPhaseInfo(2, i);
//			paramKohonenPanel.repaint();
			try {
				Thread.sleep(WAIT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void run(double[] inputVector, double factor, int neighborRadius) {
		if (neighborRadius > 1) {
//			System.out.println(neighborRadius);
		}
		if (neighborRadius < 1) {
			neighborRadius = 1;
		}

		int winnerOneDimIndex = -1;
		double min = 0.0;
		double max = Double.MAX_VALUE;

		Object[] multiDimArray = (Object[]) neuronArrayWrapper.getArray();
		for (int i=0; i<multiDimArray.length; i++) {
			
			Neuron neuron = neuronArrayWrapper.getPseudo(i);
			List<Synapse> synapseList = neuron.getIncomingSynapses();
			for (Synapse inputSynapse : synapseList) {
				int index = inputSynapse.getFromNeuron().getId();
				min += Math.pow(inputVector[index] - inputSynapse.getWeight(), 2.0D);
			}
//				Synapse synapse = synapseMatrix.getSynapse(j, i);
			// euclidic distance ?!?
				
			
	
			// für jedes output neuron, ermittele gewinner neuron
			// und speichere indizes (x,y)
			if (min < max) {
				winnerOneDimIndex = i;
				max = min;
			}
			min = 0.0;
		}
		int[] indices = neuronArrayWrapper.getMultiDimIndices(winnerOneDimIndex);
		

		List<Integer> neighborIndices = neuronArrayWrapper.getNeighborForAllDims(neighborRadius, indices);

		for (int neighbor : neighborIndices) {
			Neuron neuron = neuronArrayWrapper.getPseudo(neighbor);
			List<Synapse> synapseList = neuron.getIncomingSynapses();
			for (Synapse inputSynapse : synapseList) {
				int index = inputSynapse.getFromNeuron().getId();
				inputSynapse.setWeight(factor * (inputVector[index]-inputSynapse.getWeight()));
//				listener.update(neighbor, index, inputSynapse.getWeight());
			}
//				Synapse synapse = synapseMatrix.getSynapse(j, neighbor);			
		}
		if (listener != null) {
			listener.update();

		}
		
	}

	private double[] createRandomVector(double min, double max) {
		double[] randomVector = new double[inputLayerSize];
		Random r = new Random();
		for (int i=0; i<inputLayerSize; i++) {
			randomVector[i] = r.nextDouble() * (max - min) + min;
		}
		return randomVector;
	}

	public void addChangeListener(Board3D board) {
		listener = board;
	}
	
}
