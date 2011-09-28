package de.unikassel.ann.algo;

import java.util.List;

import de.unikassel.ann.model.DataPair;
import de.unikassel.ann.model.DataPairSet;
import de.unikassel.ann.model.Layer;
import de.unikassel.ann.model.NetError;
import de.unikassel.ann.model.Network;
import de.unikassel.ann.model.Neuron;
import de.unikassel.ann.model.Synapse;
import de.unikassel.ann.strategy.Strategy;

public class BackPropagation extends TrainingModule implements WorkModule {

	private boolean batchLearning = false;
	private Double momentum;
	private Double learnRate;

	public BackPropagation() {
		this(0.35, 0.8);
	}

	public BackPropagation(final Double learnRate, final Double momentum) {
		this.learnRate = learnRate;
		this.momentum = momentum;
	}

	@Override
	public void work(Network net, final DataPairSet testData) {
		if (net == null) {
			net = config.getNetwork();
		}
		for (DataPair p : testData.getPairs()) {
			forwardStep(net, p);
			net.setOutputToPair(p.getIdeal());
		}
	}

	@Override
	public boolean validateDataSet(final Network net, final DataPairSet testData) {
		DataPair pair = testData.getPairs().get(0);
		int inputSize = net.getInputSizeIgnoringBias();
		int outputSize = net.getOutputSize();
		if (pair.getInput().length == inputSize && pair.getIdeal().length == outputSize) {
			return true;
		}
		throw new IllegalArgumentException("test dataset does not match for topology");
	}

	private void forwardStep(final Network net, final DataPair pair) {

		if (net.isTrainable() == false) {
			throw new IllegalArgumentException("net not finalized yet");
		}
		net.setInputLayerValues(pair.getInput());

		for (Layer l : net.getLayers()) {
			for (Neuron n : l.getNeurons()) {
				// calculate netnput (not for input layer)
				if (l.equals(net.getInputLayer())) {
					continue;
				}
				// calculate netnput
				List<Synapse> synapseList = n.getIncomingSynapses();
				Double sum = 0.0d;
				for (Synapse s : synapseList) {
					sum += s.getWeight() * s.getFromNeuron().getValue();
				}
				if (n.isBias() == false) {
					n.activate(sum);
				}

			}
		}
	}

	@Override
	public void train(final DataPairSet trainingData) {
		config.initWeights(); // TODO: do it in finalyze!!!!
		netError = new NetError(this, trainingData);
		Network net = config.getNetwork();
		validateDataSet(net, trainingData);
		while (true) {
			// try {
			// Thread.sleep(1);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			trainNow = true;
			if (config.shouldRestartTraining()) {
				train(trainingData); // restar training
				trainNow = false;
				return;
			}
			if (config.shouldStopTraining()) {
				trainNow = false;
				return;
			}

			for (Strategy s : config.getStrategies()) {
				s.preIteration();
			}

			for (DataPair pair : trainingData.getPairs()) {
				forwardStep(net, pair);
				calculateDeltaAndUpdateWeights(net, pair);
				currentStep++;
			}
			if (batchLearning) {
				updateWeights(net); // offline training
			}

			double tmpError = netError.calculateRMS();
			currentImprovement = currentError - tmpError;
			currentError = tmpError;
			config.notifyError(currentError);
			// currentSingleError = netError.calculateSingleRMS();
			netError.reset();
			currentIteration++;

			for (Strategy s : config.getStrategies()) {
				s.postIteration();
			}
		}

	}

	private void calculateDeltaAndUpdateWeights(final Network net, final DataPair pair) {
		List<Layer> reversedLayers = net.reverse();
		for (Layer l : reversedLayers) {
			if (l.equals(net.getInputLayer())) {
				break;
			}
			if (l.equals(net.getOutputLayer())) {
				calculateOutputError(l, pair);
			} else {
				calculateError(l);
			}
			calculateWeightDelta(l); // for offline learning
			if (batchLearning == false) {
				updateWeights(l); // online training
			}
		}
	}

	private void updateWeights(final Network net) {
		List<Layer> reversedLayers = net.reverse();
		for (Layer l : reversedLayers) {
			updateWeights(l);
		}
	}

	private void calculateOutputError(final Layer outputLayer, final DataPair pair) {
		List<Neuron> neuronList = outputLayer.getNeurons();
		Double[] ideal = pair.getIdeal();
		Double rmseError = 0.0;
		for (int i = 0; i < ideal.length; i++) {
			Neuron n = neuronList.get(i);
			Double o = n.getValue();
			Double t = ideal[i];
			// ((t - o) * o * (1 - o)
			double diff = t - o;
			// double delta = o * (1-o) * errorFactor;
			// derivate:= (1.0 - o * o)
			double delta = n.getActivationFunction().derivate(o) * diff;
			n.setDelta(delta);
			netError.updateError(t, o);
			rmseError += Math.pow(diff, 2);
		}
	}

	private void calculateError(final Layer currentLayer) {
		for (Neuron n : currentLayer.getNeurons()) {
			double diffSum = 0.d;
			for (Synapse s : n.getOutgoingSynapses()) {
				diffSum += s.getWeight() * s.getToNeuron().getDelta();
			}
			Double o = n.getValue();
			// derivate:= (1.0 - o * o)
			double delta = n.getActivationFunction().derivate(o) * diffSum;
			// double delta = o * ( 1 - o) * errorFactor;
			n.setDelta(delta);
		}
	}

	private void calculateWeightDelta(final Layer l) {
		for (Neuron n : l.getNeurons()) {
			for (Synapse s : n.getIncomingSynapses()) {
				double offlineDelta = s.getToNeuron().getDelta() * s.getFromNeuron().getValue();
				s.updateBatchDelta(offlineDelta);
			}
		}
	}

	private void updateWeights(final Layer l) {
		for (Neuron n : l.getNeurons()) {
			for (Synapse s : n.getIncomingSynapses()) {
				Double oldDeltaWeight = s.getDeltaWeight();
				Double delta = null;
				if (batchLearning) {
					delta = learnRate * s.getBatchDelta() + momentum * oldDeltaWeight;
					s.resetBatchDelta();
				} else {
					delta = learnRate * s.getToNeuron().getDelta() * s.getFromNeuron().getValue() + momentum * oldDeltaWeight;
				}
				Double oldWeight = s.getWeight();
				s.setWeight(oldWeight + delta);
				s.setDeltaWeight(delta);
			}
		}
	}

	/**
	 * @return the bATCH_LEARNING
	 */
	public boolean isBatchMode() {
		return batchLearning;
	}

	/**
	 * @param bATCH_LEARNING
	 *            the bATCH_LEARNING to set
	 */
	public void setBatchMode(final boolean batchMode) {
		batchLearning = batchMode;
	}

	// public static void printStep(Network net, DataPair pair) {
	// forwardStep(net, pair);
	//
	// StringBuilder sb = new StringBuilder();
	// int index = 0;
	// for (Neuron n : net.getOutputLayer().getNeurons()) {
	// sb.append(n.getOutputValue());
	// sb.append( " / ");
	// sb.append(pair.getIdeal()[index]);
	// sb.append("\n");
	// index++;
	// }
	// System.out.println(sb.toString());
	// }

}
