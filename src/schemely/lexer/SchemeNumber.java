package schemely.lexer;

import dk.brics.automaton.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Colin Fleming
 */
public class SchemeNumber
{
  static final Map<String, Automaton> decimalAutomata = new HashMap<String, Automaton>();
  static final Map<String, Automaton> hexAutomata = new HashMap<String, Automaton>();
  static final Map<String, Automaton> octalAutomata = new HashMap<String, Automaton>();
  static final Map<String, Automaton> binaryAutomata = new HashMap<String, Automaton>();

  static
  {
    decimal("digit", "[0-9]");
    decimal("radix", "(\\#d)?");

    hex("digit", "[0-9a-f]");
    hex("radix", "\\#x");

    octal("digit", "[0-7]");
    octal("radix", "\\#o");

    binary("digit", "[0-1]");
    binary("radix", "\\#b");

    all("exactness", "(\\#i|\\#e)?");
    all("sign", "[-+]?");

    all("exponent", "(e|s|f|d|l)<sign><digit>+");
    all("suffix", "(<exponent>?)");
    all("prefix", "(<exactness><radix>)|(<radix><exactness>)");
    all("uinteger", "(<digit>+\\#*)");

    decimal("decimal",
            "(<uinteger><exponent>)|" +
            "(\\.<digit>+\\#*<suffix>)|" +
            "(<digit>+\\.<digit>*\\#*<suffix>)|" +
            "(<digit>+\\#+\\.\\#*<suffix>)");

    hex("decimal", BasicAutomata.makeEmpty());
    octal("decimal", BasicAutomata.makeEmpty());
    binary("decimal", BasicAutomata.makeEmpty());

    all("ureal", "(<uinteger>|(<uinteger>/<uinteger>)|<decimal>)");
    all("real", "(<sign><ureal>)");
    all("imag", "(<ureal>i)");
    all("complex",
        "<real>|" + "(<real>\\@<real>)|" + "(<real>\\+<imag>)|" + "(<real>\\-<imag>)|" + "(\\+<imag>)|" + "(\\-<imag>)");

    all("number", "(<prefix><complex>)");
  }

  static RunAutomaton number()
  {
    Automaton number = BasicOperations.union(Arrays.asList(decimalAutomata.get("number"),
                                                           hexAutomata.get("number"),
                                                           octalAutomata.get("number"),
                                                           binaryAutomata.get("number")));
    return new RunAutomaton(number);
  }

  private static void all(String name, String regexp)
  {
    decimal(name, regexp);
    hex(name, regexp);
    octal(name, regexp);
    binary(name, regexp);
  }

  private static void decimal(String name, String regexp)
  {
    decimalAutomata.put(name, new RegExp(regexp).toAutomaton(decimalAutomata));
  }

  private static void hex(String name, String regexp)
  {
    hex(name, new RegExp(regexp).toAutomaton(hexAutomata));
  }

  private static void hex(String name, Automaton automaton)
  {
    hexAutomata.put(name, automaton);
  }

  private static void octal(String name, String regexp)
  {
    octal(name, new RegExp(regexp).toAutomaton(octalAutomata));
  }

  private static void octal(String name, Automaton automaton)
  {
    octalAutomata.put(name, automaton);
  }

  private static void binary(String name, String regexp)
  {
    binary(name, new RegExp(regexp).toAutomaton(binaryAutomata));
  }

  private static void binary(String name, Automaton automaton)
  {
    binaryAutomata.put(name, automaton);
  }

  private static void dumpAll()
  {
    for (String machine : Arrays.asList("digit",
                                        "radix",
                                        "exponent",
                                        "suffix",
                                        "prefix",
                                        "uinteger",
                                        "decimal",
                                        "ureal",
                                        "real",
                                        "imag",
                                        "complex",
                                        "number"))
    {
      try
      {
        FileOutputStream stream = new FileOutputStream("decimal_" + machine + ".dot");
        Automaton automaton = decimalAutomata.get(machine).clone();
        automaton.minimize();
        automaton.reduce();
        stream.write(automaton.toDot().getBytes());
        stream.close();
      }
      catch (IOException e)
      {
        e.printStackTrace(System.err);
      }
    }
  }

}
