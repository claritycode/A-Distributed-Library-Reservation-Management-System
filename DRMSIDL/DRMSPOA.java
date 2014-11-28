package DRMSIDL;

/**
 * Interface definition: DRMS.
 * 
 * @author OpenORB Compiler
 */
public abstract class DRMSPOA extends org.omg.PortableServer.Servant
        implements DRMSOperations, org.omg.CORBA.portable.InvokeHandler
{
    public DRMS _this()
    {
        return DRMSHelper.narrow(_this_object());
    }

    public DRMS _this(org.omg.CORBA.ORB orb)
    {
        return DRMSHelper.narrow(_this_object(orb));
    }

    private static String [] _ids_list =
    {
        "IDL:DRMSIDL/DRMS:1.0"
    };

    public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte [] objectId)
    {
        return _ids_list;
    }

    public final org.omg.CORBA.portable.OutputStream _invoke(final String opName,
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler)
    {

        if (opName.equals("addBook")) {
                return _invoke_addBook(_is, handler);
        } else if (opName.equals("createAccount")) {
                return _invoke_createAccount(_is, handler);
        } else if (opName.equals("getNonReturners")) {
                return _invoke_getNonReturners(_is, handler);
        } else if (opName.equals("reserveBook")) {
                return _invoke_reserveBook(_is, handler);
        } else if (opName.equals("reserveInterLibrary")) {
                return _invoke_reserveInterLibrary(_is, handler);
        } else if (opName.equals("setDuration")) {
                return _invoke_setDuration(_is, handler);
        } else if (opName.equals("studentLogin")) {
                return _invoke_studentLogin(_is, handler);
        } else {
            throw new org.omg.CORBA.BAD_OPERATION(opName);
        }
    }

    // helper methods
    private org.omg.CORBA.portable.OutputStream _invoke_createAccount(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        String arg3_in = _is.read_string();
        String arg4_in = _is.read_string();
        String arg5_in = _is.read_string();
        int arg6_in = _is.read_long();

        int _arg_result = createAccount(arg0_in, arg1_in, arg2_in, arg3_in, arg4_in, arg5_in, arg6_in);

        _output = handler.createReply();
        _output.write_long(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_studentLogin(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();

        boolean _arg_result = studentLogin(arg0_in, arg1_in);

        _output = handler.createReply();
        _output.write_boolean(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_reserveBook(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();

        boolean _arg_result = reserveBook(arg0_in, arg1_in);

        _output = handler.createReply();
        _output.write_boolean(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_addBook(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();

        boolean _arg_result = addBook(arg0_in, arg1_in);

        _output = handler.createReply();
        _output.write_boolean(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_getNonReturners(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        int arg0_in = _is.read_long();

        String _arg_result = getNonReturners(arg0_in);

        _output = handler.createReply();
        _output.write_string(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_setDuration(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        int arg1_in = _is.read_long();

        boolean _arg_result = setDuration(arg0_in, arg1_in);

        _output = handler.createReply();
        _output.write_boolean(_arg_result);

        return _output;
    }

    private org.omg.CORBA.portable.OutputStream _invoke_reserveInterLibrary(
            final org.omg.CORBA.portable.InputStream _is,
            final org.omg.CORBA.portable.ResponseHandler handler) {
        org.omg.CORBA.portable.OutputStream _output;
        String arg0_in = _is.read_string();
        String arg1_in = _is.read_string();
        String arg2_in = _is.read_string();
        String arg3_in = _is.read_string();

        boolean _arg_result = reserveInterLibrary(arg0_in, arg1_in, arg2_in, arg3_in);

        _output = handler.createReply();
        _output.write_boolean(_arg_result);

        return _output;
    }

}
