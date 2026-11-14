package dev.zeddevstuff.crafted.ui;

@FunctionalInterface
public interface BindingExtractor
{
	BindingScope extract(Object value);
}