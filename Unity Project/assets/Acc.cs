using UnityEngine;
using System.Collections;

public class Acc : MonoBehaviour {
	
	Quaternion target;
	float smooth = 25f;
	bool snowfall;
	// Use this for initialization
	void Start () {		
		target = new Quaternion(transform.rotation.x, transform.rotation.y, transform.rotation.z, transform.rotation.w);
		
		string snowvar = PlayerPrefs.GetString("snow");
		snow(snowvar);
	}
	
	// Update is called once per frame
	void Update () {
		transform.rotation =  Quaternion.Lerp(transform.rotation,target, Time.deltaTime * smooth);
	}
	
	public void changescene(string no){
		try{
			
			int result = 0;
			
			if (int.TryParse(no, out result))
				Application.LoadLevel(result);
			else
				Application.LoadLevel(no);
			
			snow(snowfall.ToString());
		}catch(System.Exception ex){
			changescene("0");
			//displayToast(ex.Message);
		}
	}
	
    public void changesmooth (string smoothness){
		try{
			float.TryParse(smoothness,out smooth);
		}catch(System.Exception ex){
			//displayToast(ex.Message);
		}
	}
	
	public void snow(string val){
		try{
			bool check = true;
			bool.TryParse(val, out check);
			
			snowfall = check;
			
			PlayerPrefs.SetString("snow", snowfall.ToString());
			PlayerPrefs.Save();
			
			GameObject[] snowemitter = GameObject.FindGameObjectsWithTag("snow");
			if (snowemitter != null){
				foreach(GameObject go in snowemitter)
					if (go.particleEmitter != null)
						go.particleEmitter.emit = snowfall;
			}
		}catch(System.Exception ex){
		//	displayToast(ex.Message);
		}
	}
	
	public void rotate (string rot){
		try{
		string[] values = rot.Split(':');
		
		float x = 0;float.TryParse(values[0], out x);
		float y = 0;float.TryParse(values[1], out y);
		float z = 0;float.TryParse(values[2], out z);
		
		//target.eulerAngles = new Vector3 (norm(x), norm(y), norm(z));	
			target.eulerAngles = new Vector3(x,y,z);
		}catch(System.Exception ex){
			//displayToast(ex.Message);
			target.eulerAngles = new Vector3(0,0,0);
		}
	}
	
	private float norm (float val){
		while (val > 360 || val < 0){
			if (val > 360)
				val -= 360;
			else if (val < 0)
				val += 360;
		}
		return val;
	}
	
//	private void displayToast (string message){
//		AndroidJavaClass Toast = new AndroidJavaClass("android.widget.Toast");
//		AndroidJavaClass unityPlayerClass = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
//		
//		AndroidJavaObject currentActivity = unityPlayerClass.GetStatic<AndroidJavaObject> ("currentActivity");
//		
//		System.Object[]args = new System.Object[3];
//		int num = 1;
//		
//		args[0] = currentActivity;
//		args[1] = num;
//		args[2] = message;
//		
//		Toast.CallStatic("makeText",args);
//	}
	
//	void OnGUI()
//{
//    GUILayout.BeginHorizontal();
//    if(GUILayout.Button("set to 0.0"))
//    {
//		rotate ("-5:0:0");
//    }
//    if(GUILayout.Button("set to 0.5"))
//    {
//		rotate ("5:0:0");
//    }
//		if(GUILayout.Button("set to 0.0"))
//    {
//		rotate ("0:-5:0");
//    }
//    if(GUILayout.Button("set to 0.5"))
//    {
//		rotate ("0:5:0");
//    }
//   
//    GUILayout.EndHorizontal();
//}
}
