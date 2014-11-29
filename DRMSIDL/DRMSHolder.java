package DRMSIDL;

/**
 * Holder class for : DRMS
 * 
 * @author OpenORB Compiler
 */
final public class DRMSHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal DRMS value
     */
    public DRMSIDL.DRMS value;

    /**
     * Default constructor
     */
    public DRMSHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public DRMSHolder(DRMSIDL.DRMS initial)
    {
        value = initial;
    }

    /**
     * Read DRMS from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = DRMSHelper.read(istream);
    }

    /**
     * Write DRMS into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        DRMSHelper.write(ostream,value);
    }

    /**
     * Return the DRMS TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return DRMSHelper.type();
    }

}
