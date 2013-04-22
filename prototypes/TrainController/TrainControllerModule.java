package TLTTC;
import java.util.*;
import java.util.concurrent.*;

public class TrainControllerModule extends Worker implements Runnable, constData
{
  private Module name = Module.trainController;
  private Hashtable<Integer, TrainController> controllers;
  private LinkedBlockingQueue<Message> msgs;
  
  private int trainID;
  private TrainController tc;
  
  private TrainContainer trainContainer;
  
  
  public TrainControllerModule()
  {
    controllers = new Hashtable<Integer, TrainController>();
    msgs = new LinkedBlockingQueue<Message>();
  }
  
  
  public void run()
  {
    while(true)
    {
      if(msgs.peek() != null)
      {
        Message m = msgs.poll();
        if(name == m.getDest())
        {
          //System.out.println("\nRECEIVED MSG: source->"+m.getSource() + " : dest->"+m.getDest()+"\n");
          if(m.getData() != null && m.getData().containsKey("trainID"))
          {
            trainID = (int)(m.getData().get("trainID"));
            if (controllers.containsKey(trainID))
            {
              tc = controllers.get(trainID); // Local TrainController
            }
        
            switch (m.getType())
            {
              case Sat_TnCnt_Request_Traversed_Block_Stats: //???????????
                
                break;
              case Sat_TnCt_ReceiptConfirm_Traversed_Block_Stats: //??????????
                
                break;
              case MBO_TnCt_Send_Moving_Block_Authority: // Moving block authority from CTC 
                tc.setMovingBlockAuth((double)(m.getData().get("authority")));
                break;
              case TcCt_TnCt_Send_Fixed_Block_Authority: // Fixed block authority from track controller
                tc.setFixedBlockAuth((double)(m.getData().get("authority")));
                break;
              case TcMd_TnCt_Confirm_Occupancy_Return_Block_Stats: //????????????
                
                break;
              case TcMd_TnCt_Confirm_Depopulation: //???????????
                
                break; 
              case CTC_TnCt_Send_Manual_FixedBlock: // Manual fixed block from CTC
                tc.setCtcFixedBlockAuth((double)(m.getData().get("authority")));
                break;
              case CTC_TnCt_Send_Manual_Speed: // Manual velocity from CTC
                tc.setCtcOperatorVelocity((double)(m.getData().get("velocity")));
                break;
              case TcMd_TnCt_Send_Track_Gnd_State: // Underground state from track model
                tc.setUnderground((boolean)(m.getData().get("state")));
                break;
              case TcMd_TnCt_Send_Station_State: // Station state from track model
  			tc.setInStation((boolean)(m.getData().get("state")));
				break;
              case TcMd_TnCt_Send_Station_Name: // Next station name from track model
                tc.setNextStation((String)(m.getData().get("stationName")));
                break;
              case TcMd_TnCt_Send_Track_Speed_Limit: // Track speed limit from track model
                tc.setTrackLimit((double)(m.getData().get("speedLimit")));
                break;
              case CTC_TnCt_Send_Manual_MovingBlock: // Manual moving block authority from CTC
                tc.setCtcMovingBlockAuth((double)(m.getData().get("authority")));
                break;
            }
          }
          else
          {
            System.out.println("ERROR! No train ID in message sent to train controller!");
          }
        }
        else
        {
          //System.out.println("PASSING MSG ~ (source : " + m.getSource() + "), (step : " + name + "), (dest : " + m.getDest()+"), (type : " + m.getType()+")");
          m.updateSender(name);
          Environment.passMessage(m);
        }
      }
    }
  }
  
  
  public void setMsg(Message m)
  {
    msgs.add(m);
  }
  
  
  public void send(Message m)
  {
    //System.out.println("SENDING MSG: start->"+m.getSource() + " : dest->"+m.getDest()+" " + m.getType() + "\n");
    Environment.passMessage(m);
  }
  
  
  public void init(TrainContainer t)
  {
    trainContainer = t;
  }
  
  // Methods for Train Model's use
  public TrainController createTrainController(int t)
  {
    TrainController newTrainController = new TrainController(t, trainContainer.getTrain(t));
    controllers.put(t, newTrainController);
    return newTrainController;
  }
  
  
  public void destroyTrainController(int t)
  {
    // Todo: remove from dropdown list of trains in GUI here
    controllers.remove(t);
  }
}
