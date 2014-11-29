package idl;


/**
* idl/ReplicaManagerHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from idl/ReplicaManager.idl
* Wednesday, November 26, 2014 10:37:55 PM EST
*/

abstract public class ReplicaManagerHelper
{
  private static String  _id = "IDL:idl/ReplicaManager:1.0";

  public static void insert (org.omg.CORBA.Any a, idl.ReplicaManager that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static idl.ReplicaManager extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (idl.ReplicaManagerHelper.id (), "ReplicaManager");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static idl.ReplicaManager read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ReplicaManagerStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, idl.ReplicaManager value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static idl.ReplicaManager narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof idl.ReplicaManager)
      return (idl.ReplicaManager)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      idl._ReplicaManagerStub stub = new idl._ReplicaManagerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static idl.ReplicaManager unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof idl.ReplicaManager)
      return (idl.ReplicaManager)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      idl._ReplicaManagerStub stub = new idl._ReplicaManagerStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}