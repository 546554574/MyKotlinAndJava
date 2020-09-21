package com.bigkoo.pickerview.adapter;


import com.contrarywind.adapter.WheelAdapter;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapterDay implements WheelAdapter {

	private int minValue;
	private int maxValue;

	/**
	 * Constructor
	 * @param minValue the wheel min value
	 * @param maxValue the wheel max value
	 */
	public NumericWheelAdapterDay(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public Object getItem(int index) {
		if (index >= 1 && index < getItemsCount()) {
			int value = minValue + index - 1;
			return value+"日";
		}else if(index == 0){
			return "整月";
		}
		return 0;
	}

	@Override
	public int getItemsCount() {
		return maxValue - minValue + 2;
	}
	
	@Override
	public int indexOf(Object o){
		try {
			return (int)o - minValue;
		} catch (Exception e) {
			return -1;
		}

	}
}
