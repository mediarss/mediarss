package rss.configuration.dao;

import org.springframework.stereotype.Repository;
import rss.ems.dao.BaseDaoJPA;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Michael Dikman
 * Date: 24/05/12
 * Time: 11:05
 */
@Repository
public class SettingsDaoImpl extends BaseDaoJPA<Setting> implements SettingsDao {

	@Override
	protected Class<? extends Setting> getPersistentClass() {
		return Setting.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSettings(String key) {
		Setting settingEntity = getSettingEntity(key);
		return settingEntity == null ? null : settingEntity.getValue();
	}

	private Setting getSettingEntity(String key) {
		Map<String, Object> params = new HashMap<>(1);
		params.put("key", key);
		return uniqueResult(super.<Setting>findByNamedQueryAndNamedParams("Setting.findByKey", params));
	}

	@Override
	public void setSettings(String key, String value) {
		// set existing setting or create a new one
		Setting settingEntity = getSettingEntity(key);
		if (settingEntity != null) {
			settingEntity.setValue(value);
		} else {
			settingEntity = new Setting();
			settingEntity.setKey(key);
			settingEntity.setValue(value);
			super.persist(settingEntity);
		}
	}
}