package com.shinetvbox.vod.manager;

import com.shinetvbox.vod.certercontrol.ControlCenter;
import com.shinetvbox.vod.dao.DownSongProcess;
import com.shinetvbox.vod.dao.PlaySong;
import com.shinetvbox.vod.dao.SelectSong;
import com.shinetvbox.vod.db.SongInfo;
import com.shinetvbox.vod.service.wechat.WechatService;

import java.util.List;

public class SongPlayManager {

	static boolean downsongFlag = true;
	/**
	 * 将歌曲添加到已选列表
	 * @param song
	 */
	public static void addSong(SongInfo song) {
		if(null == song) return;
		if (ControlCenter.getIsVideoOpening()) return;
        if(getCurrentPlaySongId().equals( song.song_id )) return;
		if(SelectSong.getInstance().addSelected(song)){
			if(WechatService.getInstance()!=null){
				WechatService.getInstance().sendSelectSongList();
			}
		}
	}

	/**
	 * 从已选列表中删除此歌曲
	 * @param song_id
	 */
	public static void delSong(String song_id) {
		if(null == song_id)
			return;
		if(SelectSong.getInstance().delSelected(song_id)){
			if(WechatService.getInstance()!=null){
				WechatService.getInstance().sendSelectSongList();
			}
		}
	}

	/**
	 * 此歌曲置顶优先播放
	 * @param song
	 */
	public static void prioritySong(SongInfo song) {
		if(null == song) return;
        if(getCurrentPlaySongId().equals( song.song_id )) return;
		if(SelectSong.getInstance().prioritySong(song)){
			if(WechatService.getInstance()!=null){
				WechatService.getInstance().sendSelectSongList();
			}
		}
	}

	/**
	 * 立刻播放此歌曲
	 * @param song
	 */
	public static void playSong(SongInfo song) {
		if(null == song) return;
		if(SelectSong.getInstance().playSong(song)){
			if(WechatService.getInstance()!=null){
				WechatService.getInstance().sendSelectSongList();
			}
		}
	}

	/**
	 * 获取当前正在播放歌曲id
	 * @return
	 */
	public static String getCurrentPlaySongId(){
		String songid = "";
		if(PlaySong.getCurPlaySong() != null){
			songid = PlaySong.getCurPlaySong().getsongid();
		}
		return songid;
	}

	/**
	 * 获取当前正在播放的歌曲名
	 * @return
	 */
	public static String getCurrentPlaySongName(){
		String songname = "";
		if(PlaySong.getCurPlaySong() != null){
			songname = PlaySong.getCurPlaySong().getsongname();
		}
		return songname;
	}

	/**
	 * 根据歌曲ID获取此歌曲在已选歌曲中的索引
	 * @param songid
	 */
	public static int getSelectedSongIndex(String songid){
		int index = -1;
		if(SelectSong.getInstance() != null){
			index = SelectSong.getInstance().GetIndexById(songid);;
		}
		return index;
	}

	/**
	 * 获取下一首播放歌曲名称
	 * @return
	 */
    public static String getNextPlaySongName() {
        String songname = "";
        if(SelectSong.getInstance() != null){
            songname = SelectSong.getInstance().getnextsongname();
        }
        return songname;
    }

	/**
	 * 获取已选歌曲数量
	 * @return
	 */
	public static int getSelectSongCount() {
		int num = 0;
		if(SelectSong.getInstance() != null){
			num = SelectSong.getInstance().getSelectSongConut();
		}
		return num;
	}

	/**
	 * 获取已选歌曲数据
	 * @param page 页数
	 * @param number 每页数量
	 */
	public static List<SongInfo> getSelectSongData(int page,int number) {
		if(SelectSong.getInstance() != null){
			return SelectSong.getInstance().getSelectSongListByPage(page, number);
		}
		return null;
	}

	/**
	 * 已选歌曲重新排序
	 * @return
	 */
	public static void reorderSelectSongList() {
		if(SelectSong.getInstance() != null){
			SelectSong.getInstance().upsetSelected();
		}
	}

	/**
	 * 清空已选歌曲
	 * @return
	 */
	public static void clearSelectSongList() {
		if(SelectSong.getInstance() != null){
			SelectSong.getInstance().cleanSelected();
		}
	}

	/**
	 * 获取已唱歌曲数据
	 * @return
	 */
	public static int getSungSongCount() {
		int num = 0;
		if(SelectSong.getInstance() != null){
			num = SelectSong.getInstance().getSungSongConut();
		}
		return num;
	}

	/**
	 * 获取已唱歌曲数据
	 * @param page 页数
	 * @param number 每页数量
	 */
	public static List<SongInfo> getSungSongData(int page,int number) {
		if(SelectSong.getInstance() != null){
			return SelectSong.getInstance().getSungSongListByPage(page, number);
		}
		return null;
	}
	/**
	 * 清空已选歌曲
	 * @return
	 */
	public static void clearSungSongList() {
		if(SelectSong.getInstance() != null){
			SelectSong.getInstance().cleanSungSong();
		}
	}


	/**
	 * 添加到下载歌曲列表
	 */
	public static void addDownSong(SongInfo songInfo) {
		if(downsongFlag){
			SongDownloadManager.addDownSong(songInfo);
		}else{
			if(DownSongProcess.getInstance() != null){
				DownSongProcess.getInstance().addDownSong(songInfo,false);
			}
		}
	}

	/**
	 * 歌曲列表中选择优先下载
	 */
	public static void addDownSongPriority(SongInfo songInfo) {
		if(downsongFlag){
			SongDownloadManager.priorityDownSong(songInfo);
		}else{
			if(DownSongProcess.getInstance() != null){
				DownSongProcess.getInstance().addDownSong(songInfo,true);
			}
		}
	}

	/**
	 * 下载列表中歌曲置顶下载
	 * @return
	 */
	public static void priorityDownloadSong(SongInfo songInfo) {
		if(downsongFlag){
			SongDownloadManager.priorityDownSong(songInfo);
		}else{
			if(DownSongProcess.getInstance() != null){
				DownSongProcess.getInstance().priorityDownSong(songInfo);
			}
		}
	}
	/**
	 * 删除下载歌曲
	 * @return
	 */
	public static void delDownloadSong(SongInfo songInfo) {
		if(downsongFlag){
			SongDownloadManager.delDownSong(songInfo);
		}else{
			if(DownSongProcess.getInstance() != null){
				DownSongProcess.getInstance().delDownSong(songInfo);
			}
		}
	}

	/**
	 * 获取下载歌曲数量
	 * @return
	 */
	public static int getDownloadSongCount() {
		int num = 0;
		if(downsongFlag){
			num = SongDownloadManager.getDownloadSongConut();
		}else{
			if(DownSongProcess.getInstance() != null){
				num = DownSongProcess.getInstance().getDownloadSongConut();
			}
		}
		return num;
	}
	/**
	 * 根据歌曲ID获取此歌曲在已选歌曲中的索引
	 * @param songid
	 */
	public static int getDownloadSongIndex(String songid){
		int index = -1;
		if(downsongFlag){
			index = SongDownloadManager.GetIndexById(songid);
		}else{
			if(DownSongProcess.getInstance() != null){
				index = DownSongProcess.getInstance().GetIndexById(songid);
			}
		}
		return index;
	}

	/**
	 * 获取当前正在下载歌曲ID
	 */
	public static String getCurrentDownloadSongId(){
		String id = "0";
		if(downsongFlag){
			id = SongDownloadManager.getCurrentSongId();
		}else{
			if(DownSongProcess.getInstance() != null){
				id = DownSongProcess.getInstance().getCurrentSongId();
			}
		}
		return id;
	}
	/**
	 * 根据歌曲ID获取此歌曲在已选歌曲中的索引
	 * @param songid
	 */
	public static int getDownloadSongStatus(String songid){
		if(downsongFlag){
			return SongDownloadManager.GetStatusById(songid);
		}else{
			if(DownSongProcess.getInstance() != null){
				return DownSongProcess.getInstance().GetStatusById(songid);
			}
		}
		return 0;
	}
	/**
	 * 获取下载歌曲数据
	 * @param page 页数
	 * @param number 每页数量
	 */
	public static List<SongInfo> getDownloadSongData(int page,int number) {
		if(downsongFlag){
			return SongDownloadManager.getDownlistSongListByPage(page, number);
		}else{
			if(DownSongProcess.getInstance() != null){
				return DownSongProcess.getInstance().getDownlistSongListByPage(page, number);
			}
		}
		return null;
	}
	/**
	 * 清空下载歌曲列表
	 * @return
	 */
	public static void clearDownloadSongList() {
		if(downsongFlag){
			SongDownloadManager.clearDownloadList();
		}else{
			if(DownSongProcess.getInstance() != null){
				DownSongProcess.getInstance().cleanDownloadList();
			}
		}
	}
}