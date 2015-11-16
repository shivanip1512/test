package com.cannontech.web.common.commandRequestExecution;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;

public class CommandRequestExecutionWrapperFactory {

	private CommandRequestExecutionResultDao commandRequestExecutionResultDao;

	public CommandRequestExecutionWrapper createCommandRequestExecutionWrapper(CommandRequestExecution cre) {
		return new CommandRequestExecutionWrapper(cre);
	}
	
	public class CommandRequestExecutionWrapper {
		
		private CommandRequestExecution cre;
		
		public CommandRequestExecution getCre() {
			return cre;
		}
		
		public CommandRequestExecutionWrapper(CommandRequestExecution cre) {
			this.cre = cre;
		}
		
		public int getSuccessCount() {
			return commandRequestExecutionResultDao.getSucessCountByExecutionId(cre.getId());
		}
		
		public int getFailCount() {
			return commandRequestExecutionResultDao.getFailCountByExecutionId(cre.getId());
		}
		
		public int getTotalCount() {
			return commandRequestExecutionResultDao.getCountByExecutionId(cre.getId());
		}

        public int getUnsupportedCount() {
            return commandRequestExecutionResultDao.getUnsupportedCountByExecutionId(cre.getId(), null);
        }
	}
	
	@Autowired
	public void setCommandRequestExecutionResultDao(CommandRequestExecutionResultDao commandRequestExecutionResultDao) {
		this.commandRequestExecutionResultDao = commandRequestExecutionResultDao;
	}
}
