package DRMSIDL;

/** 
 * Helper class for : DRMS
 *  
 * @author OpenORB Compiler
 */ 
public class DRMSHelper
{
    /**
     * Insert DRMS into an any
     * @param a an any
     * @param t DRMS value
     */
    public static void insert(org.omg.CORBA.Any a, DRMSIDL.DRMS t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract DRMS from an any
     *
     * @param a an any
     * @return the extracted DRMS value
     */
    public static DRMSIDL.DRMS extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return DRMSIDL.DRMSHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the DRMS TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "DRMS" );
        }
        return _tc;
    }

    /**
     * Return the DRMS IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:DRMSIDL/DRMS:1.0";

    /**
     * Read DRMS from a marshalled stream
     * @param istream the input stream
     * @return the readed DRMS value
     */
    public static DRMSIDL.DRMS read(org.omg.CORBA.portable.InputStream istream)
    {
        return(DRMSIDL.DRMS)istream.read_Object(DRMSIDL._DRMSStub.class);
    }

    /**
     * Write DRMS into a marshalled stream
     * @param ostream the output stream
     * @param value DRMS value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, DRMSIDL.DRMS value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to DRMS
     * @param obj the CORBA Object
     * @return DRMS Object
     */
    public static DRMS narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof DRMS)
            return (DRMS)obj;

        if (obj._is_a(id()))
        {
            _DRMSStub stub = new _DRMSStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to DRMS
     * @param obj the CORBA Object
     * @return DRMS Object
     */
    public static DRMS unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof DRMS)
            return (DRMS)obj;

        _DRMSStub stub = new _DRMSStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
