package dev.zeddevstuff.crafted.parsing;

import java.util.HashMap;

public record ParsedNode(String tag, HashMap<String, String> attributes, ParsedNode[] children) {}
