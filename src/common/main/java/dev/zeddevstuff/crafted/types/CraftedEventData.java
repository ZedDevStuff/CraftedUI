package dev.zeddevstuff.crafted.types;

public class CraftedEventData
{
	public final Class<?> inputType;
	public final Class<?> outputType;

	public final String type;
	public final Object data;
	private Object result;
	public Object getResult() { return outputType == null ? null : result; }
	public void setResult(Object result)
	{
		if(outputType != null && !outputType.isInstance(result))
			throw new IllegalArgumentException("Result must be of type " + outputType.getName());
		if(outputType == null)
			throw new IllegalArgumentException("Result cannot be set for an event with no output type");
		this.result = result;
	}

	public CraftedEventData(Class<?> inputType, Class<?> outputType, String type, Object data)
	{
		this.inputType = inputType;
		this.outputType = outputType;
		this.type = type;
		this.data = data;
	}
}
