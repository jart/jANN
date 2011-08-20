package de.unikassel.ann.model;

import java.util.ArrayList;
import java.util.List;

import de.unikassel.ann.model.func.ActivationFunction;
import de.unikassel.ann.model.func.SigmoidFunction;

/**
 * @author anton
 *
 */
public class Neuron {

	private Layer layer;
	
	private Integer index = -1; // local for its layer
	
	private Integer id = -1; // global for whole network
	
	private ActivationFunction activationFunction;

	private Double value; // TODO: name
		
	private Double delta;
	
	private List<Synapse> incomingSynapses;
	
	private List<Synapse> outgoingSynapses;

	private boolean bias;
	
	private static String functionPackage = SigmoidFunction.class.getPackage().getName();
	
	public Neuron(ActivationFunction standardFunc, double outputValue, boolean bias) {
		set(standardFunc, outputValue, bias);
	}

	/**
	 * @param standardFunc
	 * @param outputValue
	 * @param bias
	 */
	private void set(ActivationFunction standardFunc, double outputValue,
			boolean bias) {
		this.value = outputValue;
		this.bias = bias;
		if (bias) {
			this.value = 1.0d;
		}
		this.activationFunction = standardFunc;
		incomingSynapses = new ArrayList<Synapse>();
		outgoingSynapses = new ArrayList<Synapse>();
	}
	
	public Neuron(ActivationFunction function, boolean bias) {
		this(function, 0.0d, bias);
	}

	public Neuron(String function, boolean bias) {
		try {
			Class<?> clazz = Class.forName(functionPackage+"."+function);
			ActivationFunction functinonInstance = (ActivationFunction) clazz.newInstance();
			set(functinonInstance, 0.0d, bias);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public List<Synapse> getIncomingSynapses() {
		return incomingSynapses;
	}
	
	public List<Synapse> getOutgoingSynapses() {
		return outgoingSynapses;
	}

	public void setOutputValue(Double val) {
		if (bias) {
			throw new IllegalAccessError("cannot set input value for bias neuron");
		}
		value = val;
	}
	public Double getOutputValue() {
		return value;
	}

	public void addOutgoingSynapse(Synapse synapse) {
		outgoingSynapses.add(synapse);
		if ((synapse.getFromNeuron() != null && synapse.getFromNeuron().equals(this)) == false) {
			synapse.setFromNeuron(this);
		}
	}
	
	public void addIncomingSynapse(Synapse synapse) {
		incomingSynapses.add(synapse);
		if ((synapse.getToNeuron() != null && synapse.getToNeuron().equals(this)) == false) {
			synapse.setToNeuron(this);
		}
	}
	
	/**
	 * Sets the delta or error value
	 * @param error
	 */
	public void setDelta(double error) {
		this.delta = error;
	}

	/**
	 * Calculates the activation function with the value and set the result as outpuvalue<br>
	 * {@link #value} = f (val)
	 */
	public void activate(Double val) {
		if (bias) {
			value = 1.0d;
		} else {
			value = activationFunction.activate(val);
		}
	}
	

	public void setLayer(Layer layer) {
		this.layer = layer;
		if (layer.getNeurons() != null && layer.getNeurons().contains(this) == false) {
			layer.addNeuron(this);
		}
		
	}

	public Layer getLayer() {
		return layer;
	}

	public void setIndex(int size) {
		index = size;
	}
	
	public Integer getIndex() {
		return index;
	}

	/**
	 * @return the delta or error
	 */
	public double getDelta() {
		return delta;
	}

	public boolean isBias() {
		return bias;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getPosition());
		sb.append(bias ? " (B)" : "");
		
		sb.append("output = ");
		sb.append(value == null ? "N/A" : value);
		sb.append("\n");
		return sb.toString();
	}
	
	public String getPosition() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(layer.getIndex());
		sb.append("]");
		
		sb.append("[");
		sb.append(index);
		sb.append("]");
		
		sb.append("{");
		sb.append(id);
		sb.append("}");
		return sb.toString();
	}

	public void setId(int identifier) {
		this.id = identifier;
		
	}

	public Integer getId() {
		return id;
	}

	public String getFunctionName() {
		return activationFunction.getClass().getSimpleName();
	}

	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}


}
