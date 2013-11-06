package com.huddle.handle.adaptors;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.huddle.handle.android.R;
import com.huddle.handle.android.DiscussionActivity;
import com.huddle.handle.android.FileActivity;
import com.huddle.handle.android.UserActivity;
import com.huddle.handle.android.WhiteboardActivity;
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.client.model.WhatsNewItem;
import com.huddle.handle.tasks.BackgroundTask;
import com.huddle.handle.tasks.DownloadTask;
import com.huddle.handle.tasks.ShareTask;

public class WhatsNewListAdapter extends ArrayAdapter<WhatsNewItem> 
	implements AdapterView.OnItemClickListener, OnCreateContextMenuListener {

	public static final String DETAILS = "Details";
	public static final String SHARE = "Share";
	public static final String OPEN = "Open";
	int resource;
	Context context;
	Activity aty;
	List<WhatsNewItem> items;
	String title;
	boolean dashboard;
	public static WhatsNewItem clickedItem;
	
	public WhatsNewListAdapter (Context _context, int _resource, 
			List<WhatsNewItem> _items, Activity activity, String title) {
		super(_context, _resource, _items);
		context = _context;
		resource = _resource;
		items = _items;
		aty = activity;
		this.title = title;
		dashboard = title.equals(aty.getResources().getString(R.string.my_dashboard));
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		clickedItem = items.get(info.position - (dashboard ? 1 : 0));
		String targetType = clickedItem.getTargetType();
		if (targetType == null) {
			return;
		}
		
		menu.setHeaderTitle(targetType);
		
		if (targetType.equals(WhatsNewItem.TARGET_TYPE_COMMENT)) {
			return;
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_DOCUMENT)) {
			menu.add(DETAILS);
			menu.add(SHARE);
		}

		menu.add(OPEN);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		clickedItem = items.get(position - (dashboard ? 1 : 0));
		if (clickedItem.getTargetType().equals(WhatsNewItem.TARGET_TYPE_DOCUMENT)) {
			startChildActivity(DETAILS);
		} else {
			startChildActivity(OPEN);
		}
	}


	public void startChildActivity(String action) {
		String targetType = clickedItem.getTargetType();
		
		if (targetType == null) {
			return;
		}
		
		Intent intent = new Intent();
		intent.putExtra(aty.getString(R.string.whats_new), title);
		intent.putExtra("targetId", clickedItem.getTargetId());
		intent.putExtra("workspaceId", clickedItem.getWorkspaceId());
		intent.putExtra("workspaceTitle", clickedItem.getWorkspaceTitle());
		intent.putExtra("targetText", clickedItem.getTargetText());
		
		if (targetType.equals(WhatsNewItem.TARGET_TYPE_WHITEBOARD)) {
			intent.setClass(aty, WhiteboardActivity.class);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_DISCUSSION) || targetType.equals(WhatsNewItem.TARGET_TYPE_DISCUSSION_POST)) {
			intent.setClass(aty, DiscussionActivity.class);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_USER)) {
			intent.setClass(aty, UserActivity.class);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_DOCUMENT)) {
			if (action.equals(OPEN) || action.equals(SHARE)) {
				try {
					FileSystemObject fso = new FileSystemObject(clickedItem.getTargetId(), clickedItem.getTargetText());
					BackgroundTask task;
					if (action.equals(OPEN)) {
						task= new DownloadTask(aty, fso);
					} else {
						task = new ShareTask(aty, fso);
					}
					task.execute(aty);	
					return;
				} catch (IOException e) {}
			} else {
				intent.setClass(aty, FileActivity.class);
			}
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_TASK)) {
			return;
			/* Can't open task when it's not in the task list, so don't open other tasks here
			intent.setClass(aty, TaskActivity.class);
			*/
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_COMMENT)) {
			return;
		} else {
			return;
		}
		aty.startActivity(intent);
	}
	
	@Override
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		WhatsNewItem item = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			headerView = new LinearLayout(context);
			
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)context.getSystemService(inflater);
			vi.inflate(resource, headerView, true);
		} else {
			headerView = (LinearLayout)convertView;
		}
			
		TextView entry = (TextView)headerView.findViewById(R.id.whatsnew_item);
		ImageView picture = (ImageView)headerView.findViewById(R.id.whatsnew_list_picture);


		String row = getRowTitle(item.getTargetType(), item.getTargetText(), item.getVersionNumber(),
				item.getFormattedDate(), item.getWorkspaceTitle(), item.getDescription(), item.getDisplayName());
		entry.setText(Html.fromHtml(row));

		String targetType = item.getTargetType();
		Drawable img = null;
		if (targetType.equals(WhatsNewItem.TARGET_TYPE_WHITEBOARD)) {
			img = context.getResources().getDrawable(R.drawable.whiteboard);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_DISCUSSION)) {
			img = context.getResources().getDrawable(R.drawable.discussion);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_DISCUSSION_POST)) {
			img = context.getResources().getDrawable(R.drawable.post);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_USER)) {
			img = context.getResources().getDrawable(R.drawable.user);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_DOCUMENT)) {
			img = context.getResources().getDrawable(R.drawable.file);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_TASK)) {
			img = context.getResources().getDrawable(R.drawable.task);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_COMMENT)) {
			img = context.getResources().getDrawable(R.drawable.comment);
		} else if (targetType.equals(WhatsNewItem.TARGET_TYPE_MEETING)) {
			img = context.getResources().getDrawable(R.drawable.meeting);
		}
		picture.setImageDrawable(img);

		return headerView;
	}
	
    private String getRowTitle(String targetType, String targetText, String version, String date, String workspace, 
    		String description, String name) {
        StringBuffer sb = new StringBuffer();
        
        boolean inWkspc = title != aty.getResources().getString(R.string.my_dashboard);

        if (WhatsNewItem.TARGET_TYPE_DISCUSSION_POST.equals(targetType)) {
            createLine(sb, "Post", targetText, " was posted in the ", workspace, inWkspc, true);
        } else if (WhatsNewItem.TARGET_TYPE_DISCUSSION.equals(targetType)) {
            createLine(sb, targetType, targetText, " was started in the ", workspace, inWkspc, true);
        } else if (WhatsNewItem.TARGET_TYPE_DOCUMENT.equals(targetType)) {
            if ("1".equals(version)) {
	            createLine(sb, targetType, targetText, " was added to the ", workspace, inWkspc, true);
            } else {
	            createLine(sb, targetType, targetText, " was updated in the ", workspace, inWkspc, false);
            }
        } else if (WhatsNewItem.TARGET_TYPE_USER.equals(targetType)) {
	            createLine(sb, targetType, targetText, " was added to the ", workspace, inWkspc, true);
        } else if (WhatsNewItem.TARGET_TYPE_WHITEBOARD.equals(targetType)) {
            if ("1".equals(version)) {
	            createLine(sb, targetType, targetText, " was added to the ", workspace, inWkspc, true);
            } else {
	            createLine(sb, targetType, targetText, " was updated in the ", workspace, inWkspc, false);
            }
        } else if (WhatsNewItem.TARGET_TYPE_TASK.equals(targetType)) {
            createLine(sb, targetType ,targetText, " was created in the ", workspace, inWkspc, true);
        } else if (WhatsNewItem.TARGET_TYPE_MEETING.equals(targetType)) {
            createLine(sb, targetType ,targetText, " was scheduled in the ", workspace, inWkspc, true);
        } else if (WhatsNewItem.TARGET_TYPE_COMMENT.equals(targetType)) {
            createLine(sb, "", targetType, " was added to the file ", targetText, false, true);
        } else {
            sb.append(targetText);
        }
        
        if (WhatsNewItem.TARGET_TYPE_COMMENT.equals(targetType)) {
        	if (!inWkspc) {
		        sb.append(" in ").append(workspace).append(" workspace");
        	}
			sb.append("<br/><small>").append(description).append("</small></font>");
        } else {
	        sb.append(" workspace");
        }
        
        if (name != null && !name.equals("null")) {
			sb.append("<br/><font color=\"#808080\"><small>").append(name).append("</small></font>");
        }
        
		sb.append("<br/><font color=\"#C0C0C0\"><small>").append(date).append("</small></font>");
		
        return sb.toString();
    }
    
    private void createLine(StringBuffer sb, String type, String targetText, String middle, String workspace, 
    		boolean inWorkspace, boolean nw) {
    	if (nw) {
	    	sb.append("New ");
    	}
    	sb.append(type).append(" ");
        sb.append(targetText).append(middle);
        if (!inWorkspace)
        	sb.append(workspace);
    }
    
    public List<WhatsNewItem> getItems() {
    	return items;
    }
}