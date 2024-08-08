package axl.compiler.utils;

// TODO
public interface Serializable {

    byte[] serialize();

    Object parse(byte[] buffer);

}
