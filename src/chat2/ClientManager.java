package chat2;

import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.api.so.ISharedObjectService;

public class ClientManager
{
  private String name;
  private boolean persistent;
  
  public ClientManager(String name, boolean persistent)
  {
    this.name = name;
    this.persistent = persistent;
  }
  
  private ISharedObject getSharedObject(IScope scope)
  {
    ISharedObjectService service = (ISharedObjectService)
      ScopeUtils.getScopeService(scope, 
      ISharedObjectService.class, 
      false);
    return service.getSharedObject(scope, this.name, this.persistent);
  }
  
  public void addClient(IScope scope, String username, String uid)
  {
    ISharedObject so = getSharedObject(scope);
    so.setAttribute(uid, username);
  }
  
  public String removeClient(IScope scope, String uid)
  {
    ISharedObject so = getSharedObject(scope);
    if (!so.hasAttribute(uid)) {
      return null;
    }
    String username = so.getStringAttribute(uid);
    so.removeAttribute(uid);
    return username;
  }
  
  public String returnClient(IScope scope, String uid)
  {
    ISharedObject so = getSharedObject(scope);
    if (!so.hasAttribute(uid)) {
      return null;
    }
    String username = so.getStringAttribute(uid);
    
    return username;
  }
}
