package com.sjm.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bimface.sdk.BimfaceClient;
import com.bimface.sdk.bean.response.FileBean;
import com.sjm.bean.BimModel;
import com.sjm.bean.BimModelExample;
import com.sjm.dao.BimModelMapper;
import com.sjm.service.BimModelService;

@Service
public class BimModelServiceImpl implements BimModelService {
	
	@Autowired
	private BimModelMapper bimModelMapper;

	@Override
	public void saveBim(MultipartFile uploadFile) {
		// TODO Auto-generated method stub
		// 1.初始化BIMFACE
		String appKey = "9w6dHaSBqwuwe05FTAyL1ZGbJYFEYUWe";
		String appSecret = "ZrJvOZBkgx3u4hm8F5izj1rscYoHDxGV";
		BimfaceClient bimfaceClient = new BimfaceClient(appKey, appSecret);

		String name = uploadFile.getOriginalFilename();
		long contentLength = uploadFile.getSize();
		FileBean fileBean = null;
		Long fileId = null;
		
		try {
			// 2.上传模型
			fileBean = bimfaceClient.upload(name, contentLength, uploadFile.getInputStream());

			// 3.获取文件id
			fileId = fileBean.getFileId();
			
			// 4.发起文件转换
			bimfaceClient.translate(fileId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		BimModel record = new BimModel();
		record.setFileId(fileId);
		record.setName(name);
		record.setCreateTime(new Date());
		record.setSize(contentLength);
		bimModelMapper.insertSelective(record);
	}

	@Override
	public List<BimModel> getModels(BimModel model) {
		// TODO Auto-generated method stub
		BimModelExample example = new BimModelExample();
		example.createCriteria().andNameLike("%" + model.getName() + "%");
		List<BimModel> models = bimModelMapper.selectByExample(example);
		return models;
	}

	@Override
	public BimModel getModel(Integer id) {
		// TODO Auto-generated method stub
		return bimModelMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updateModel(BimModel model) {
		// TODO Auto-generated method stub
		bimModelMapper.updateByPrimaryKeySelective(model);
	}
	
	@Override
	public void delModel(Integer id) {
		// TODO Auto-generated method stub
		bimModelMapper.deleteByPrimaryKey(id);
	}
}
