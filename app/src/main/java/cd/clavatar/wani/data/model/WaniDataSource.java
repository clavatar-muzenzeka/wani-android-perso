package cd.clavatar.wani.data.model;

import android.content.Context;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/* renamed from: cd.clavatar.wani.data.model.WaniDataSource */
public class WaniDataSource {
    private static WaniDataSource INSTANCE;
    private Context mContext;

    public static WaniDataSource getInstance(Context pContext) {
        if (INSTANCE == null) {
            synchronized (WaniDataSource.class) {
                INSTANCE = new WaniDataSource();
            }
        }
        INSTANCE.setContext(pContext);
        return INSTANCE;
    }

    private void setContext(Context pContext) {
        this.mContext = pContext;
    }

    public void insertWaniParametersOrUpdateByRemoteId(Date synced, ArrayList<WaniParameter> waniParameters) {
        Iterator<WaniParameter> it = waniParameters.iterator();
        while (it.hasNext()) {
            WaniParameter cWaniParameter = it.next();
            cWaniParameter.setSynced(synced);
            WaniParameter correpondingWaniParameters = Select.from(WaniParameter.class).where(Condition.prop("_id").eq(cWaniParameter.get_id())).first();
            if (!(correpondingWaniParameters == null || correpondingWaniParameters.getId() == null)) {
                cWaniParameter.setId(correpondingWaniParameters.getId());
                correpondingWaniParameters.update();
            }
        }
        SugarRecord.saveInTx(waniParameters);
    }

    public void insertLocationsOrUpdateByRemoteId(Date synced, ArrayList<Location> locations) {
        Iterator<Location> it = locations.iterator();
        while (it.hasNext()) {
            Location cLocation = it.next();
            cLocation.setSynced(synced);
            Location correpondingLocations = Select.from(Location.class).where(Condition.prop("_id").eq(cLocation.get_id())).first();
            if (!(correpondingLocations == null || correpondingLocations.getId() == null)) {
                cLocation.setId(correpondingLocations.getId());
            }
        }
        SugarRecord.saveInTx(locations);
    }

    public void insertPicturesOrUpdateByRemoteId(Date synced, ArrayList<Picture> Pictures) {
        Iterator<Picture> it = Pictures.iterator();
        while (it.hasNext()) {
            Picture cPicture = it.next();
            cPicture.setSynced(synced);
            Picture correpondingPictures = Select.from(Picture.class).where(Condition.prop("_id").eq(cPicture.get_id())).first();
            if (correpondingPictures != null) {
                cPicture.setId(correpondingPictures.getId());
            }
        }
        SugarRecord.saveInTx(Pictures);
    }

    public void insertPeoplesOrUpdateByRemoteId(Date synced, ArrayList<People> Peoples) {
        Iterator<People> it = Peoples.iterator();
        while (it.hasNext()) {
            People cPeople = it.next();
            cPeople.setSynced(synced);
            People correpondingPeoples = Select.from(People.class).where(Condition.prop("_id").eq(cPeople.get_id())).first();
            if (correpondingPeoples != null) {
                cPeople.setId(correpondingPeoples.getId());
                cPeople.setLocaleIdProfile(correpondingPeoples.getLocaleIdProfile());
            } else {
                Picture existant = getPictureByRemoteId(cPeople.getIdProfile());
                if (existant != null) {
                    cPeople.setLocaleIdProfile(existant.getId());
                }
            }
        }
        SugarRecord.saveInTx(Peoples);
    }

    public void insertPaimentTypesOrUpdateByRemoteId(Date synced, ArrayList<PaimentType> PaimentTypes) {
        Iterator<PaimentType> it = PaimentTypes.iterator();
        while (it.hasNext()) {
            PaimentType cPaimentType = it.next();
            cPaimentType.setSynced(synced);
            PaimentType correpondingPaimentTypes = Select.from(PaimentType.class).where(Condition.prop("_id").eq(cPaimentType.get_id())).first();
            if (correpondingPaimentTypes != null) {
                cPaimentType.setId(correpondingPaimentTypes.getId());
            }
        }
        SugarRecord.saveInTx(PaimentTypes);
    }

    public void insertCompactUsersOrUpdateByRemoteId(Date synced, ArrayList<CompactUser> compactUsers) {
        Iterator<CompactUser> it = compactUsers.iterator();
        while (it.hasNext()) {
            CompactUser cCompactUser = it.next();
            cCompactUser.setSynced(synced);
            People mp = cCompactUser.getIdPeople();
            Location ml = cCompactUser.getIdLocation();
            new ArrayList();
            new ArrayList();
            if (mp != null) {
                cCompactUser.setIdPeople(getCompactPeopleByRemoteId(cCompactUser.getIdPeople().get_id()));
            }
            if (ml != null) {
                cCompactUser.setIdLocation(getLocationByRemoteId(cCompactUser.getIdLocation().get_id()));
            }
            CompactUser correpondingCompactUser = Select.from(CompactUser.class).where(Condition.prop("_id").eq(cCompactUser.get_id())).first();
            if (correpondingCompactUser != null) {
                cCompactUser.setId(correpondingCompactUser.getId());
            }
        }
        SugarRecord.saveInTx(compactUsers);
    }

    public void insertCompactEncaissementsOrUpdateByRemoteId(Date synced, ArrayList<CompactEncaissement> CompactEncaissements) {
        Iterator<CompactEncaissement> it = CompactEncaissements.iterator();
        while (it.hasNext()) {
            CompactEncaissement cCompactEncaissement = it.next();
            cCompactEncaissement.setSynced(synced);
            CompactUser mu = cCompactEncaissement.getIdEncaisser();
            if (mu != null) {
                ArrayList<CompactUser> arlp = new ArrayList<>();
                arlp.add(mu);
                insertCompactUsersOrUpdateByRemoteId(synced, arlp);
            }
            CompactEncaissement correpondingCompactEncaissement = Select.from(CompactEncaissement.class).where(Condition.prop("_id").eq(cCompactEncaissement.get_id())).first();
            if (correpondingCompactEncaissement != null) {
                cCompactEncaissement.setId(correpondingCompactEncaissement.getId());
                cCompactEncaissement.setIdEncaisser(correpondingCompactEncaissement.getIdEncaisser());
            }
        }
        SugarRecord.saveInTx(CompactEncaissements);
    }

    public void insertCompactPaiementsOrUpdateByRemoteId(Date synced, ArrayList<CompactPaiement> compactPaiements) {
        Iterator<CompactPaiement> it = compactPaiements.iterator();
        while (it.hasNext()) {
            CompactPaiement cCompactPaiement = it.next();
            cCompactPaiement.setSynced(synced);
            CompactUser mu = cCompactPaiement.getIdReceiver();
            People cp = cCompactPaiement.getIdPayer();
            PaimentType mpt = cCompactPaiement.getIdPaiementType();
            CompactEncaissement me = cCompactPaiement.getEncaissed();
            if (cp != null) {
                cCompactPaiement.setIdPayer(getCompactPeopleByRemoteId(cCompactPaiement.getIdPayer().get_id()));
            }
            if (mu != null) {
                cCompactPaiement.setIdReceiver(getCompactUserByRemoteId(cCompactPaiement.getIdReceiver().get_id()));
            }
            if (mpt != null) {
                cCompactPaiement.setIdPaiementType(getPaiementTypeByRemoteId(cCompactPaiement.getIdPaiementType().get_id()));
            }
            if (me != null) {
                cCompactPaiement.setEncaissed(getCompactEncaissementByRemoteId(cCompactPaiement.getEncaissed().get_id()));
            }
            CompactPaiement correpondingCompactPaiements = Select.from(CompactPaiement.class).where(Condition.prop("_id").eq(cCompactPaiement.get_id())).first();
            if (correpondingCompactPaiements != null) {
                cCompactPaiement.setId(correpondingCompactPaiements.getId());
            }
        }
        SugarRecord.saveInTx(compactPaiements);
    }

    public void insertVaccinsOrUpdateByRemoteId(Date synced, ArrayList<Vaccin> Vaccins) {
        Iterator<Vaccin> it = Vaccins.iterator();
        while (it.hasNext()) {
            Vaccin cVaccin = it.next();
            cVaccin.setSynced(synced);
            Vaccin correpondingVaccins = Select.from(Vaccin.class).where(Condition.prop("_id").eq(cVaccin.get_id())).first();
            if (correpondingVaccins != null) {
                cVaccin.setId(correpondingVaccins.getId());
            }
        }
        SugarRecord.saveInTx(Vaccins);
    }

    public void insertChecksOrUpdateByRemoteId(Date synced, ArrayList<Check> Checks) {
        Iterator<Check> it = Checks.iterator();
        while (it.hasNext()) {
            Check cCheck = it.next();
            cCheck.setSynced(synced);
            if (cCheck.getIdPaiement() != null) {
                cCheck.setIdPaiement(getCompactPaiementByRemoteId(cCheck.getIdPaiement().get_id()));
            }
            Check correpondingChecks = Select.from(Check.class).where(Condition.prop("_id").eq(cCheck.get_id())).first();
            if (correpondingChecks != null) {
                cCheck.setId(correpondingChecks.getId());
                cCheck.setLocaleStoryId(correpondingChecks.getLocaleStoryId());
            }
        }
        SugarRecord.saveInTx(Checks);
    }

    public void insertChecksOrUpdateByRemoteId(Date synced, List<Check> Checks) {
        for (Check cCheck : Checks) {
            cCheck.setSynced(synced);
            if (cCheck.getIdPaiement() != null) {
                cCheck.setIdPaiement(getCompactPaiementByRemoteId(cCheck.getIdPaiement().get_id()));
            }
            Check correpondingChecks = Select.from(Check.class).where(Condition.prop("_id").eq(cCheck.get_id())).first();
            if (correpondingChecks != null) {
                cCheck.setId(correpondingChecks.getId());
                cCheck.setLocaleStoryId(correpondingChecks.getLocaleStoryId());
            }
        }
        SugarRecord.saveInTx(Checks);
    }

    public void insertVaccinOccurencesOrUpdateByRemoteId(Date synced, ArrayList<VaccinOccurence> VaccinOccurences) {
        Iterator<VaccinOccurence> it = VaccinOccurences.iterator();
        while (it.hasNext()) {
            VaccinOccurence cVaccinOccurence = it.next();
            cVaccinOccurence.setSynced(synced);
            CompactPaiement mu = cVaccinOccurence.getIdPaiement();
            Vaccin mv = cVaccinOccurence.getIdVaccin();
            if (mu != null) {
                cVaccinOccurence.setIdPaiement(getCompactPaiementByRemoteId(mu.get_id()));
            }
            if (mv != null) {
                cVaccinOccurence.setIdVaccin(getVaccinByRemoteId(mv.get_id()));
            }
            VaccinOccurence correpondingVaccinOccurences = Select.from(VaccinOccurence.class).where(Condition.prop("_id").eq(cVaccinOccurence.get_id())).first();
            if (correpondingVaccinOccurences != null) {
                cVaccinOccurence.setId(correpondingVaccinOccurences.getId());
                cVaccinOccurence.setLocaleCarnetId(correpondingVaccinOccurences.getLocaleCarnetId());
            }
        }
        SugarRecord.saveInTx(VaccinOccurences);
    }

    public void insertVaccinOccurencesOrUpdateByRemoteId(Date synced, List<VaccinOccurence> VaccinOccurences) {
        for (VaccinOccurence cVaccinOccurence : VaccinOccurences) {
            cVaccinOccurence.setSynced(synced);
            VaccinOccurence correpondingVaccinOccurences = Select.from(VaccinOccurence.class).where(Condition.prop("_id").eq(cVaccinOccurence.get_id())).first();
            CompactPaiement mu = cVaccinOccurence.getIdPaiement();
            Vaccin mv = cVaccinOccurence.getIdVaccin();
            if (mu != null) {
                cVaccinOccurence.setIdPaiement(getCompactPaiementByRemoteId(mu.get_id()));
            }
            if (mv != null) {
                cVaccinOccurence.setIdVaccin(getVaccinByRemoteId(mv.get_id()));
            }
            if (correpondingVaccinOccurences != null) {
                cVaccinOccurence.setId(correpondingVaccinOccurences.getId());
                cVaccinOccurence.setLocaleCarnetId(correpondingVaccinOccurences.getLocaleCarnetId());
            }
        }
        SugarRecord.saveInTx(VaccinOccurences);
    }

    public void insertCompactStatussOrUpdateByRemoteId(Date synced, ArrayList<CompactStatus> CompactStatuss) {
        Iterator<CompactStatus> it = CompactStatuss.iterator();
        while (it.hasNext()) {
            CompactStatus cCompactStatus = it.next();
            cCompactStatus.setSynced(synced);
            CompactPaiement mu = cCompactStatus.getIdPaiement();
            CompactUser mcu = cCompactStatus.getAuthor();
            Location ml = cCompactStatus.getIdLocation();
            if (mu != null) {
                cCompactStatus.setIdPaiement(getCompactPaiementByRemoteId(mu.get_id()));
            }
            if (mcu != null) {
                cCompactStatus.setAuthor(getCompactUserByRemoteId(mcu.get_id()));
            }
            if (ml != null) {
                cCompactStatus.setIdLocation(getLocationByRemoteId(ml.get_id()));
            }
            CompactStatus correpondingCompactStatuss = Select.from(CompactStatus.class).where(Condition.prop("_id").eq(cCompactStatus.get_id())).first();
            if (correpondingCompactStatuss != null) {
                cCompactStatus.setId(correpondingCompactStatuss.getId());
                cCompactStatus.setLocaleStoryId(correpondingCompactStatuss.getLocaleStoryId());
            }
        }
        SugarRecord.saveInTx(CompactStatuss);
    }

    public void insertCompactStatussOrUpdateByRemoteId(Date synced, List<CompactStatus> CompactStatuss) {
        for (CompactStatus cCompactStatus : CompactStatuss) {
            cCompactStatus.setSynced(synced);
            CompactStatus correpondingCompactStatuss = Select.from(CompactStatus.class).where(Condition.prop("_id").eq(cCompactStatus.get_id())).first();
            CompactPaiement mu = cCompactStatus.getIdPaiement();
            CompactUser mcu = cCompactStatus.getAuthor();
            Location ml = cCompactStatus.getIdLocation();
            if (mu != null) {
                cCompactStatus.setIdPaiement(getCompactPaiementByRemoteId(mu.get_id()));
            }
            if (mcu != null) {
                cCompactStatus.setAuthor(getCompactUserByRemoteId(mcu.get_id()));
            }
            if (ml != null) {
                cCompactStatus.setIdLocation(getLocationByRemoteId(ml.get_id()));
            }
            if (correpondingCompactStatuss != null) {
                cCompactStatus.setId(correpondingCompactStatuss.getId());
                cCompactStatus.setLocaleStoryId(correpondingCompactStatuss.getLocaleStoryId());
            }
        }
        SugarRecord.saveInTx(CompactStatuss);
    }

    public void insertStorysOrUpdateByRemoteId(Date synced, ArrayList<Story> Storys) {
        Iterator<Story> it = Storys.iterator();
        while (it.hasNext()) {
            Story cStory = it.next();
            cStory.setSynced(synced);
            Story correpondingStorys = Select.from(Story.class).where(Condition.prop("_id").eq(cStory.get_id())).first();
            if (correpondingStorys != null) {
                cStory.setId(correpondingStorys.getId());
                cStory.update();
            } else {
                cStory.save();
            }
            List<CompactStatus> mcs = cStory.getStatusStory();
            List<Check> mcks = cStory.getCheckStory();
            for (CompactStatus cs : mcs) {
                CompactStatus ccs = Select.from(CompactStatus.class).where(Condition.prop("_id").eq(cs.get_id())).first();
                if (ccs != null) {
                    ccs.setLocaleStoryId(cStory.getId());
                    ccs.update();
                }
            }
            for (Check cs2 : mcks) {
                Check ccs2 = Select.from(Check.class).where(Condition.prop("_id").eq(cs2.get_id())).first();
                if (ccs2 != null) {
                    ccs2.setLocaleStoryId(cStory.getId());
                    ccs2.update();
                }
            }
        }
        Date date = synced;
    }

    public void insertCompactCarnetsOrUpdateByRemoteId(Date synced, List<CompactCarnet> CompactCarnets) {
        WaniDataSource waniDataSource = this;
        for (CompactCarnet cCompactCarnet : CompactCarnets) {
            cCompactCarnet.setSynced(synced);
            int i = 1;
            CompactCarnet correpondingCompactCarnets = Select.from(CompactCarnet.class).where(Condition.prop("_id").eq(cCompactCarnet.get_id())).first();
            People mp = cCompactCarnet.getIdPeople();
            CompactStatus mcs = cCompactCarnet.getCarnetStatus();
            Story ms = cCompactCarnet.getStory();
            if (mp != null) {
                cCompactCarnet.setIdPeople(waniDataSource.getCompactPeopleByRemoteId(mp.get_id()));
            }
            if (mcs != null) {
                cCompactCarnet.setCarnetStatus(waniDataSource.getCompactStatusByRemoteId(mcs.get_id()));
            }
            if (ms != null) {
                cCompactCarnet.setStory(waniDataSource.getStoryByRemoteId(ms.get_id()));
            }
            if (correpondingCompactCarnets == null) {
                cCompactCarnet.save();
            } else if (!cCompactCarnet.getStatus().booleanValue()) {
                correpondingCompactCarnets.delete();
            } else {
                cCompactCarnet.setId(correpondingCompactCarnets.getId());
                cCompactCarnet.update();
            }
            List<VaccinOccurence> mvos = cCompactCarnet.getVaccins();
            if (mvos != null) {
                for (VaccinOccurence mvo : mvos) {
                    Select<VaccinOccurence> from = Select.from(VaccinOccurence.class);
                    Condition[] conditionArr = new Condition[i];
                    conditionArr[0] = Condition.prop("_id").eq(mvo.get_id());
                    VaccinOccurence correspondingVaccinOccurence = from.where(conditionArr).first();
                    if (correspondingVaccinOccurence != null) {
                        correspondingVaccinOccurence.setLocaleCarnetId(cCompactCarnet.getId());
                        correspondingVaccinOccurence.update();
                    }
                    i = 1;
                }
            }
            waniDataSource = this;
        }
        Date date = synced;
    }

    public List<Location> fetchSyncableNewLocations() {
        return Select.from(Location.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<Location> fetchSyncableOldLocations() {
        return Picture.findWithQuery(Location.class, "SELECT * FROM LOCATION WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<Picture> fetchSyncableNewPictures() {
        return Select.from(Picture.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<Picture> fetchSyncableOldPictures() {
        return Picture.findWithQuery(Picture.class, "SELECT * FROM PICTURE WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<People> fetchSyncableNewPeoples() {
        return Select.from(People.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<People> fetchSyncableOldPeoples() {
        return Picture.findWithQuery(People.class, "SELECT * FROM PEOPLE WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<CompactUser> fetchSyncableNewCompactUsers() {
        return Select.from(CompactUser.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<CompactUser> fetchSyncableOldCompactUsers() {
        return Picture.findWithQuery(CompactUser.class, "SELECT * FROM COMPACT_USER WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<PaimentType> fetchSyncableNewPaimentTypes() {
        return Select.from(PaimentType.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<PaimentType> fetchSyncableOldPaimentTypes() {
        return Picture.findWithQuery(PaimentType.class, "SELECT * FROM PAIMENT_TYPE WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<CompactPaiement> fetchSyncableNewCompactPaiements() {
        return Select.from(CompactPaiement.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<CompactPaiement> fetchSyncableOldCompactPaiements() {
        return Picture.findWithQuery(CompactPaiement.class, "SELECT * FROM COMPACT_PAIEMENT WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<Vaccin> fetchSyncableNewVaccins() {
        return Select.from(Vaccin.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<Vaccin> fetchSyncableOldVaccins() {
        return Picture.findWithQuery(Vaccin.class, "SELECT * FROM VACCIN WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<VaccinOccurence> fetchSyncableNewVaccinOccurences() {
        return Select.from(VaccinOccurence.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<VaccinOccurence> fetchSyncableOldVaccinOccurences() {
        return Picture.findWithQuery(VaccinOccurence.class, "SELECT * FROM VACCIN_OCCURENCE WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<Check> fetchSyncableNewChecks() {
        return Select.from(Check.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<Check> fetchSyncableOldChecks() {
        return Picture.findWithQuery(Check.class, "SELECT * FROM CARNET_CHECK WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<CompactStatus> fetchSyncableNewCompactStatuss() {
        return Select.from(CompactStatus.class).where(Condition.prop("_id").isNull()).list();
    }

    public List<CompactStatus> fetchSyncableOldCompactStatuss() {
        return Picture.findWithQuery(CompactStatus.class, "SELECT * FROM COMPACT_STATUS WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
    }

    public List<Story> fetchSyncableNewStorys() {
        List<Story> syncableStorys = Select.from(Story.class).where(Condition.prop("_id").isNull()).list();
        for (Story mcc : syncableStorys) {
            mcc.getStatusStory();
            mcc.getCheckStory();
        }
        return syncableStorys;
    }

    public List<Story> fetchSyncableOldStorys() {
        List<Story> syncableStorys = Picture.findWithQuery(Story.class, "SELECT * FROM STORY WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
        for (Story mcc : syncableStorys) {
            mcc.getStatusStory();
            mcc.getCheckStory();
        }
        return syncableStorys;
    }

    public List<CompactCarnet> fetchSyncableNewCompactCarnets() {
        List<CompactCarnet> syncableCompactCarnets = Select.from(CompactCarnet.class).where(Condition.prop("_id").isNull()).list();
        for (CompactCarnet mcc : syncableCompactCarnets) {
            mcc.getVaccins();
        }
        return syncableCompactCarnets;
    }

    public List<CompactCarnet> fetchSyncableOldCompactCarnets() {
        List<CompactCarnet> syncableCompactCarnets = Picture.findWithQuery(CompactCarnet.class, "SELECT * FROM COMPACT_CARNET WHERE (_id IS NOT NULL  AND SYNCED < EDITED )", new String[0]);
        for (CompactCarnet mcc : syncableCompactCarnets) {
            mcc.getVaccins();
        }
        return syncableCompactCarnets;
    }

    public Picture getPictureByRemoteId(String idProfile) {
        return Select.from(Picture.class).where(Condition.prop("_id").eq(idProfile)).first();
    }

    public Location getLocationByRemoteId(String idProfile) {
        return Select.from(Location.class).where(Condition.prop("_id").eq(idProfile)).first();
    }

    public void bindRemoteProfileToPeopleAndRawCarnet(Picture mSyncablePicture) {
        People mp = Select.from(People.class).where(Condition.prop("LOCALE_ID_PROFILE").eq(mSyncablePicture.getId())).first();
        if (mp != null) {
            mp.setIdProfile(mSyncablePicture.get_id());
            mp.save();
        }
        CompactCarnet mcc = Select.from(CompactCarnet.class).where(Condition.prop("LOCALE_PICTURE_RAW_CARNET").eq(mSyncablePicture.getId())).first();
        if (mcc != null) {
            mcc.setPictureRawCarnet(mSyncablePicture.get_id());
            mcc.save();
        }
    }

    public CompactUser getCompactUserByRemoteId(String id) {
        return Select.from(CompactUser.class).where(Condition.prop("_id").eq(id)).first();
    }

    public People getCompactPeopleByRemoteId(String id) {
        return Select.from(People.class).where(Condition.prop("_id").eq(id)).first();
    }

    public CompactCarnet getCompactCarnetByRemoteId(String id) {
        return Select.from(CompactCarnet.class).where(Condition.prop("_id").eq(id)).first();
    }

    public Story getStoryByRemoteId(String id) {
        return Select.from(Story.class).where(Condition.prop("_id").eq(id)).first();
    }

    public VaccinOccurence getVaccinOccurenceByRemoteId(String id) {
        return Select.from(VaccinOccurence.class).where(Condition.prop("_id").eq(id)).first();
    }

    public CompactStatus getCompactStatusByRemoteId(String id) {
        return Select.from(CompactStatus.class).where(Condition.prop("_id").eq(id)).first();
    }

    public PaimentType getPaiementTypeByRemoteId(String id) {
        return Select.from(PaimentType.class).where(Condition.prop("_id").eq(id)).first();
    }

    public CompactEncaissement getCompactEncaissementByRemoteId(String id) {
        return Select.from(CompactEncaissement.class).where(Condition.prop("_id").eq(id)).first();
    }

    public CompactPaiement getCompactPaiementByRemoteId(String id) {
        return Select.from(CompactPaiement.class).where(Condition.prop("_id").eq(id)).first();
    }

    public Vaccin getVaccinByRemoteId(String id) {
        return Select.from(Vaccin.class).where(Condition.prop("_id").eq(id)).first();
    }

    public Check getCheckByRemoteId(String id) {
        return Select.from(Check.class).where(Condition.prop("_id").eq(id)).first();
    }

    public CompactUser getCompactUserByUsername(String username) {
        return Select.from(CompactUser.class).where(Condition.prop("USERNAME").eq(username)).first();
    }

    public CompactCarnet getCompactCarnetByCarnetId(String newId) {
        return Select.from(CompactCarnet.class).where(Condition.prop("IDI_CARNET").eq(newId)).first();
    }
}
