package chat2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
//import org.red5.server.api.IScope;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.Red5;
import org.red5.server.api.service.IServiceCapableConnection;
import org.red5.server.api.service.ServiceUtils;
import org.red5.server.api.so.ISharedObject;
import org.slf4j.Logger;

public class Application  extends ApplicationAdapter {
  private static Logger log = Red5LoggerFactory.getLogger(Application.class, "video2");
  public HashMap<String, User> users = new HashMap();
  private ClientManager clientMgr = new ClientManager("users_so", true);
  
  public class cam_send  {
    public String id = null;
    public String status = null;
    
    public cam_send(String id, String status)
    {
      this.id = id;
      this.status = status;
    }
  }
  
  public boolean appStart(IScope app){
    log.info("AppStart");
    return super.appStart(app);
  }
  
  public void appStop() {
    log.info("appStop");
  }
  
  public boolean appConnect(IConnection conn, Object[] params) {
    boolean checkCopy = false;
    try
    {
      log.info("appConnect: " + params[0] + " " + params[1]);
    }
    catch (Exception e2)
    {
      log.info("appConnect no params");
    }
    try
    {
      Users Reg = new Users();
      checkCopy = Reg.checkCopy();
    }
    catch (Exception exc)
    {
      rejectClient("Проблема с валидацией" + exc.getMessage());
      return false;
    }
    if (checkCopy)
    {
      if (params == null)
      {
        rejectClient("Client must pass 2 params !.");
        return false;
      }
      try
      {
        IClient client = conn.getClient();//.setId((String)params[0]);
        log.info("id set");
      }
      catch (Exception e)
      {
        log.error("id not set{}", e);
      }
      String nikname = params[0].toString();
      String password = params[1].toString().toUpperCase();
      data4login d4l = new data4login("");
      if (d4l.searchIndex(nikname) < 0)
      {
        rejectClient("Пользователя не существует!");
        return false;
      }
      if (!d4l.getPass(nikname).toUpperCase().equals(password))
      {
        rejectClient("Неправильный ввод пароля!");
        return false;
      }
      String id = conn.getClient().getId();
      log.info("appClient: conn.getClient().getId()" + id + " length " + id.length());
      this.users.put(nikname, new User(id, nikname));
      this.clientMgr.addClient(this.scope, nikname, id);
      log.info("AppClient: After ClientMgr");
      ServiceUtils.invokeOnAllConnections("UserJoinRoom", new Object[] { new User(nikname) });
      return true;
    }
    rejectClient("Программа не прошла валидацию");
    return false;
  }
  
  public void appExit()
  {
    IConnection appCon = Red5.getConnectionLocal();
    appDisconnect(appCon);
  }
  
  public void appDisconnect(IConnection conn)
  {
    log.info("appDisconnect");
    IScope appScope = Red5.getConnectionLocal().getScope();
    String id = conn.getClient().getId();
    String nikname = this.clientMgr.removeClient(this.scope, id);
    User user = (User)this.users.get(nikname);
    this.users.remove(nikname);
    ServiceUtils.invokeOnAllConnections("UserLeaveRoom", new Object[] { user });
    super.appDisconnect(conn);
  }
  
  public boolean roomJoin(IClient client, IScope room)
  {
    log.info("roomJoin");
    return super.roomJoin(client, room);
  }
  
  public void change_webcam(String status)
  {
    log.info("change_webcam: " + status);
    IConnection conn = Red5.getConnectionLocal();
    String id = conn.getClient().getId();
    ServiceUtils.invokeOnAllConnections("onchange_webcam", new Object[] { new cam_send(id, status) });
    IScope appScope = Red5.getConnectionLocal().getScope();
    String pseudo = this.clientMgr.returnClient(this.scope, id);
    User user = (User)this.users.get(pseudo);
    this.users.put(pseudo, new User(user.nikname));
  }
  
  public void listStreamsToLog(String scopeName)
  {
    IScope target = Red5.getConnectionLocal().getScope();
    List<String> streamNames = getBroadcastStreamNames(target);
    for (String name : streamNames) {
      log.info(name);
    }
  }
  
  public void watch(String UserId)
  {
    log.info("watch: " + UserId);
    IConnection conn2 = Red5.getConnectionLocal();
    String uid = conn2.getClient().getId();
    IScope appScope = Red5.getConnectionLocal().getScope();
    Collection<Set<IConnection>> connecciones = appScope.getConnections();
    Iterator<IConnection> it;
    for (Iterator localIterator = connecciones.iterator(); localIterator.hasNext(); it.hasNext())
    {
      Set<IConnection> listConnection = (Set)localIterator.next();
      it = listConnection.iterator();
      continue;
      IConnection conn = (IConnection)it.next();
      String id = conn.getClient().getId();
      if ((UserId.equals(id)) && 
        ((conn instanceof IServiceCapableConnection)))
      {
        ((IServiceCapableConnection)conn).invoke("onwatch", new Object[] { uid });
        return;
      }
    }
  }
  
  public void offwatch(String UserId)
  {
    log.info("offwatch: " + UserId);
    
    IConnection conn2 = Red5.getConnectionLocal();
    String uid = conn2.getClient().getId();
    IScope appScope = Red5.getConnectionLocal().getScope();
    Collection<Set<IConnection>> connecciones = appScope.getConnections();
    Iterator<IConnection> it;
    for (Iterator localIterator = connecciones.iterator(); localIterator.hasNext(); it.hasNext())
    {
      Set<IConnection> listConnection = (Set)localIterator.next();
      it = listConnection.iterator();
      continue;
      IConnection conn = (IConnection)it.next();
      String id = conn.getClient().getId();
      if ((UserId.equals(id)) && 
        ((conn instanceof IServiceCapableConnection)))
      {
        ((IServiceCapableConnection)conn).invoke("offwatch", new Object[] { uid });
        return;
      }
    }
  }
  
  public void roomLeave(IClient client, IScope room)
  {
    log.info("roomLeave: " + client.getId());
    super.roomLeave(client, room);
  }
  
  public String getUserId()
  {
    IConnection conn2 = Red5.getConnectionLocal();
    String uid = conn2.getClient().getId();
    log.info("GetUserId: " + uid);
    
    return uid;
  }
  
  public HashMap<String, User> getUserList()
  {
    return this.users;
  }
  
  public boolean roomStart(IScope room)
  {
    log.info("roomStart");
    if (!super.roomStart(room)) {
      return false;
    }
    createSharedObject(room, "chat", false);
    ISharedObject chat = getSharedObject(room, "chat");
    
    return true;
  }
}
