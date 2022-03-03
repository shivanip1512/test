package com.eaton.builders.drsetup.loadprogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;

import com.eaton.builders.drsetup.gears.EcobeeCycleGearBuilder;
import com.eaton.builders.drsetup.gears.EcobeeSetpointGearBuilder;
import com.eaton.builders.drsetup.gears.HoneywellCycleGearBuilder;
import com.eaton.builders.drsetup.gears.ItronCycleGearBuilder;
import com.eaton.builders.drsetup.gears.TimeRefreshGearBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupEmetconCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupExpresscomCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupHoneywellCreateBuilder;
import com.eaton.builders.drsetup.loadgroup.LoadGroupItronCreateBuilder;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums.OperationalState;
import com.eaton.builders.drsetup.loadprogram.ProgramEnums.ProgramType;

public class LoadProgramCreateService {

	public static Pair<JSONObject, JSONObject> createEcobeeProgramWithCycleGear() {
		List<JSONObject> gears = new ArrayList<>();
		gears.add(EcobeeCycleGearBuilder.gearBuilder().build());

		Pair<JSONObject, JSONObject> pairLdGrp = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
				.withKwCapacity(Optional.empty())
				.create();

		JSONObject responseLdGrp = pairLdGrp.getValue1();
		int ldGrpId = responseLdGrp.getInt("id");
		List<Integer> assignedGroupIds = new ArrayList<>(List.of(ldGrpId));

		return new LoadProgramCreateBuilder.Builder(ProgramEnums.ProgramType.ECOBEE_PROGRAM, gears, assignedGroupIds)
				.withGears(gears).withName(Optional.empty())
				.withOperationalState(Optional.of(ProgramEnums.OperationalState.Automatic))
				.create();
	}

	public static Map<String, Pair<JSONObject, JSONObject>> createEcobeeProgramAllFieldsWithSetPointGear() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

		Pair<JSONObject, JSONObject> loadGrpPair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty()).create();

		JSONObject ldGrp = loadGrpPair.getValue1();
		Integer ldGrpId = ldGrp.getInt("id");

		JSONObject gear = EcobeeSetpointGearBuilder.gearBuilder().withName("TestGear").build();

		Pair<JSONObject, JSONObject> programPair = LoadProgramCreateBuilder
				.buildLoadProgram(ProgramType.ECOBEE_PROGRAM, new ArrayList<>(List.of(gear)),
						new ArrayList<>(List.of(ldGrpId)))
				.withName(Optional.empty()).withOperationalState(Optional.of(OperationalState.Manual_Only))
				.withControlWindowOneAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowOneAvailableStopTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.of(60))
				.create();

		hmap.put("LoadGroup", loadGrpPair);
		hmap.put("LoadProgram", programPair);

		return hmap;
	}

	public static Map<String, Pair<JSONObject, JSONObject>> createItronProgramAllFieldsWithItronCycleGear() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

		Pair<JSONObject, JSONObject> loadGrpPair = new LoadGroupItronCreateBuilder.Builder(Optional.empty())
				.withRelay(Optional.empty())
				.create();

		JSONObject ldGrp = loadGrpPair.getValue1();
		Integer ldGrpId = ldGrp.getInt("id");

		JSONObject gear = ItronCycleGearBuilder.gearBuilder().withName("TestItronGear").build();

		Pair<JSONObject, JSONObject> programPair = LoadProgramCreateBuilder
				.buildLoadProgram(ProgramType.ITRON_PROGRAM, new ArrayList<>(List.of(gear)),
						new ArrayList<>(List.of(ldGrpId)))
				.withName(Optional.empty()).withOperationalState(Optional.of(OperationalState.Manual_Only))
				.withControlWindowOneAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowOneAvailableStopTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.of(60))
				.create();

		hmap.put("LoadGroup", loadGrpPair);
		hmap.put("LoadProgram", programPair);

		return hmap;
	}
	
	public static Map<String, Pair<JSONObject, JSONObject>> createDirectProgramAllFieldsWithTimeRefreshGear() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

		Pair<JSONObject, JSONObject> loadGrpPair = LoadGroupEmetconCreateBuilder.buildDefaultEmetconLoadGroup()
				.create();

		JSONObject ldGrp = loadGrpPair.getValue1();
		Integer ldGrpId = ldGrp.getInt("id");

		JSONObject gear = TimeRefreshGearBuilder.gearBuilder().withName("TestTimeRefresh").build();

		Pair<JSONObject, JSONObject> programPair = LoadProgramCreateBuilder
				.buildLoadProgram(ProgramType.DIRECT_PROGRAM, new ArrayList<>(List.of(gear)),
						new ArrayList<>(List.of(ldGrpId)))
				.withName(Optional.empty()).withOperationalState(Optional.of(OperationalState.Manual_Only))
				.withControlWindowOneAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowOneAvailableStopTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.of(60))
				.create();

		hmap.put("LoadGroup", loadGrpPair);
		hmap.put("LoadProgram", programPair);

		return hmap;
	}
	
	public static Map<String, Pair<JSONObject, JSONObject>> createHoneywellProgramAllFieldsWithCycleGear() {
		HashMap<String, Pair<JSONObject, JSONObject>> hmap = new HashMap<>();

		Pair<JSONObject, JSONObject> loadGrpPair = LoadGroupHoneywellCreateBuilder.buildLoadGroup()
														.create();

		JSONObject ldGrp = loadGrpPair.getValue1();
		Integer ldGrpId = ldGrp.getInt("id");

		JSONObject gear = HoneywellCycleGearBuilder.gearBuilder().withName("TestHoneywellCycle").build();

		Pair<JSONObject, JSONObject> programPair = LoadProgramCreateBuilder
				.buildLoadProgram(ProgramType.HONEYWELL_PROGRAM, new ArrayList<>(List.of(gear)),
						new ArrayList<>(List.of(ldGrpId)))
				.withName(Optional.empty()).withOperationalState(Optional.of(OperationalState.Manual_Only))
				.withControlWindowOneAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowOneAvailableStopTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStartTimeInMinutes(Optional.of(60))
				.withcontrolWindowTwoAvailableStopTimeInMinutes(Optional.of(60))
				.create();

		hmap.put("LoadGroup", loadGrpPair);
		hmap.put("LoadProgram", programPair);

		return hmap;
			}
}