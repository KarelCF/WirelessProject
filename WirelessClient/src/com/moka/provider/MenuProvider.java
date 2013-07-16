package com.moka.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MenuProvider extends ContentProvider {

	private DBHelper helper = null;
	private static UriMatcher uriMatcher = null;
	private static final int GET_LIST = 1;
	private static final int GET_ITEM = 2;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Menus.AUTHORITY, Menus.TABLE_NAME, GET_LIST);
		uriMatcher.addURI(Menus.AUTHORITY, Menus.TABLE_NAME + "/#", GET_ITEM);
	}
	
	@Override
	public boolean onCreate() {
		helper = new DBHelper(getContext());
		helper.getReadableDatabase();
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		// 暂时不需要查看类型
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = this.helper.getWritableDatabase() ;
		long id = 0 ;
		switch(uriMatcher.match(uri)) {
		case GET_LIST :
			// 插入数据操作
			id = db.insert(Menus.TABLE_NAME, Menus._ID, values);
			String uriPath = uri.toString() ;
			String path = uriPath + "/" + id ;
			return Uri.parse(path) ;
		case GET_ITEM :
			return null ; 
		default:
			throw new UnsupportedOperationException("Not Support Operation :"
					+ uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// 获得可读数据库
        SQLiteDatabase db = helper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
		case GET_LIST:
			return db.query(Menus.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		case GET_ITEM:
			long id = ContentUris.parseId(uri) ;
			String where = "_id=" + id ;
			return db.query(Menus.TABLE_NAME, projection, where, selectionArgs, null, null, sortOrder);
		default:
			throw new UnsupportedOperationException("Not Support Operation :"
					+ uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// 暂时不需要更新操作
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.helper.getWritableDatabase();
		int result = 0;
		switch(uriMatcher.match(uri)) {
		case GET_LIST :
			// 删除数据操作
			result = db.delete(Menus.TABLE_NAME, selection, selectionArgs);
			return result;
		case GET_ITEM :
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id ;
			result = db.delete(Menus.TABLE_NAME, where, selectionArgs);
		default:
			throw new UnsupportedOperationException("Not Support Operation :"
					+ uri);
		}
	}

}