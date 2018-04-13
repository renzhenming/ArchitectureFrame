/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/renzhenming/Desktop/app/ArchitectureFrame/app/src/main/aidl/com/example/renzhenming/appmarket/test/GuardAidl.aidl
 */
package com.example.renzhenming.appmarket.test;
// Declare any non-default types here with import statements

public interface GuardAidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.renzhenming.appmarket.test.GuardAidl
{
private static final java.lang.String DESCRIPTOR = "com.example.renzhenming.appmarket.test.GuardAidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.renzhenming.appmarket.test.GuardAidl interface,
 * generating a proxy if needed.
 */
public static com.example.renzhenming.appmarket.test.GuardAidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.renzhenming.appmarket.test.GuardAidl))) {
return ((com.example.renzhenming.appmarket.test.GuardAidl)iin);
}
return new com.example.renzhenming.appmarket.test.GuardAidl.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.renzhenming.appmarket.test.GuardAidl
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
}
}
}
