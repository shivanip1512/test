//file that contains the command ids, commands names, and functions that connect
//user interface names with the commands themselves

//reference table for the reflection
var REF_TABLE = {ALL_CAP_CMDS:"cap",ALL_FDR_CMDS:"feeder",ALL_SUB_CMDS:"sub", ALL_TAG_CMDS:"tag"};

var ALL_CMD_TYPES = {sub:"sub", feeder:"feeder", cap:"cap", tag:"tag"};
var ALL_SUB_CMDS = {confirm_close:9, enable_sub:0, disable_sub:1, reset_op_cnt:12, v_all_banks:40, v_fq_banks:41, v_failed_banks:42, v_question_banks:43, v_disable_verify:44, v_standalone_banks:46};
var ALL_FDR_CMDS = {enable_fdr:2, disable_fdr:3, reset_op_cnt:12};
var ALL_CAP_CMDS = {confirm_open:8, open_capbank:6, close_capbank:7, bank_enable_ovuv:17, bank_disable_ovuv:18, enable_capbank:4, disable_capbank:5, reset_op_cnt:12, scan_2way_dev:24};


//same as CapBank constants
var ALL_TAG_CMDS = {standalone:"Stand Alone", switched:"Switched", 
					capEnabled:"capEnabled", capDisabled:"capDisabled", 
					feederEnabled:"feederEnabled", feederDisabled:"feederDisabled",
					subEnabled:"subEnabled", subDisabled:"subDisabled",
					capOVUVEnabled:"capOVUVEnabled", capOVUVDisabled:"capOVUVDisabled"
					};
					
function executeCommand(string) {
	args = string.split("_");
	if (args.length == 3) {
		var comm = new Command(args[1], args[2], args[0]);
		comm.execute();
	} else {
		var comm;
		if ((args.length == 4) && (args[0] == ALL_CMD_TYPES.cap)) {
			comm = new Command(args[1], args[2], args[0], true);
		} else {
			if ((args.length == 4) && (args[0] == ALL_CMD_TYPES.tag)) {
				comm = new Command(args[1], args[2], args[0]);
				comm.reason = args[3];
			}
		}
		if (comm) {
			comm.execute();
		}
	}
}
function Command(pId, cId, t, isMan, n) {
	this.paoID = pId;
	this.cmdID = cId;
	this.type = t;
	this.execute = Command_execute;
	this.createName = Command_createName;
	this.isManual = false;
	this.reason = null;
	if (isMan) {
		this.isManual = isMan;
		this.name = n;
	}
}
function Command_execute() {
	if (this.type == ALL_CMD_TYPES.sub) {
		executeSubCommand(this.paoID, this.cmdID, "");
	} else {
		if (this.type == ALL_CMD_TYPES.feeder) {
			executeFeederCommand(this.paoID, this.cmdID, "");
		} else {
			if (this.type == ALL_CMD_TYPES.tag) {
				executeReasonUpdate(this.paoID, this.cmdID, this.reason);
			} else {
				if (this.type == ALL_CMD_TYPES.cap) {
					if (this.cmdID == ALL_CAP_CMDS.reset_op_cnt) {
						var newOpcntVal = prompt("What is the new value for opcount?", "0");
						executeCapBankCommand(this.paoID, this.cmdID, newOpcntVal);
					} else {
						if (this.isManual) {						
							executeCapBankCommand(this.paoID, this.cmdID, "", true);
						} else {
							executeCapBankCommand(this.paoID, this.cmdID, "");
						}
					}
				}
			}
		}
	}
}
function Command_createName() {
	name = this.type + "_" + this.paoID + "_" + this.cmdID;
	if (this.isManual) {
		
		name += "_true";
	}
	return name;
}

//function that uses reflection to get the command name from the string
//passed to it
//for example:
//cryptic name such as cap_1731_5 will be returned as 
//disable_capbank
function getCommandVerbal (str) {
	var type = str.split("_")[0];
	var cmdID = str.split("_")[2];
	var obj = reflect (REF_TABLE, type);
	obj = eval (obj);
	return reflect (obj, cmdID);	
}


