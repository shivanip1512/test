package com.cannontech.web.admin.energyCompany.list;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.constants.YukonSelectionListOrder;
import com.cannontech.common.util.LazyList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class SelectionListDto {
    public static class Entry {
        private int entryId = 0;
        private int order = 0;
        private String text = null;
        private int definitionId = 0;
        private boolean isDeletion = false;

        public Entry() {
        }

        public Entry(int entryId, int order, String text, int definitionId) {
            this.entryId = entryId;
            this.order = order;
            this.text = text;
            this.definitionId = definitionId;
        }

        public int getEntryId() {
            return entryId;
        }

        public void setEntryId(int entryId) {
            this.entryId = entryId;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getDefinitionId() {
            return definitionId;
        }

        public void setDefinitionId(int definitionId) {
            this.definitionId = definitionId;
        }

        public boolean isDeletion() {
            return isDeletion;
        }

        public void setDeletion(boolean isDeletion) {
            this.isDeletion = isDeletion;
        }

        @Override
        public String toString() {
            return "Entry [entryId=" + entryId + ", order=" + order + ", text=" + text
                   + ", definitionId=" + definitionId + ", isDeletion=" + isDeletion + "]";
        }

        public YukonListEntry getYukonListEntry(int listId) {
            YukonListEntry retVal = new YukonListEntry();
            retVal.setEntryID(entryId);
            retVal.setListID(listId);
            retVal.setEntryOrder(order);
            retVal.setEntryText(text);
            retVal.setYukonDefID(definitionId);
            return retVal;
        }
    }

    private int listId;
    private String selectionLabel;
    private String whereIsList;
    private YukonSelectionListEnum type = null;
    private int energyCompanyId;

    private List<Entry> entries = LazyList.ofInstance(Entry.class);

    public SelectionListDto() {
    }

    public SelectionListDto(YukonSelectionList selectionList) {
        listId = selectionList.getListId();
        selectionLabel = selectionList.getSelectionLabel();
        whereIsList = selectionList.getWhereIsList();
        type = selectionList.getType();
        energyCompanyId = selectionList.getEnergyCompanyId();
        for (YukonListEntry entry : selectionList.getYukonListEntries()) {
            entries.add(new Entry(entry.getEntryID(), entry.getEntryOrder(), entry.getEntryText(),
                                  entry.getYukonDefID()));
        }

        Ordering<Entry> sorter = null;
        if (selectionList.getOrdering() != YukonSelectionListOrder.ENTRY_ORDER) {
            // We're not going to support different orderings anymore so convert them all to entry
            // order.
            sorter = new Ordering<Entry>() {
                @Override
                public int compare(Entry entry1, Entry entry2) {
                    return entry1.text.compareToIgnoreCase(entry2.text);
                }
            }.nullsFirst();
        }
        sortEntries(sorter);
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getSelectionLabel() {
        return selectionLabel;
    }

    public void setSelectionLabel(String selectionLabel) {
        this.selectionLabel = selectionLabel;
    }

    public String getWhereIsList() {
        return whereIsList;
    }

    public void setWhereIsList(String whereIsList) {
        this.whereIsList = whereIsList;
    }

    public YukonSelectionListEnum getType() {
        return type;
    }

    public void setType(YukonSelectionListEnum type) {
        this.type = type;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void sortEntries(Ordering<Entry> sorter) {
        if (sorter == null) {
            sorter = new Ordering<Entry>() {
                @Override
                public int compare(Entry entry1, Entry entry2) {
                    return entry1.order - entry2.order;
                }
            };
        }
        Collections.sort(entries, sorter);
        int index = 1;
        for (Entry entry : entries) {
            entry.order = index++;
        }
    }

    /**
     * Translate this DTO back to the YukonSelectionList for saving to the database.  The oldList
     * is used for things which should not be changed.
     */
    public YukonSelectionList getYukonSelectionList() {
        YukonSelectionList retVal = new YukonSelectionList();
        retVal.setListId(listId);
        retVal.setOrdering(YukonSelectionListOrder.ENTRY_ORDER);
        retVal.setSelectionLabel(selectionLabel);
        retVal.setWhereIsList(whereIsList);
        retVal.setType(type);
        retVal.setUserUpdateAvailable(true);
        retVal.setEnergyCompanyId(energyCompanyId);
        List<YukonListEntry> newEntries = Lists.newArrayList();
        for (Entry entry : entries) {
            if (!entry.isDeletion) {
                newEntries.add(entry.getYukonListEntry(listId));
            }
        }
        retVal.setYukonListEntries(newEntries);
        return retVal;
    }

    /**
     * Get the list of entry ids to delete.
     */
    public List<Integer> getEntryIdsToDelete() {
        List<Integer> entryIdsToDelete = Lists.newArrayList();
        for (Entry entry : entries) {
            if (entry.isDeletion && entry.entryId != 0) {
                entryIdsToDelete.add(entry.entryId);
            }
        }
        return entryIdsToDelete;
    }

    @Override
    public String toString() {
        return "SelectionListDto [listId=" + listId + ", selectionLabel=" + selectionLabel
               + ", whereIsList=" + whereIsList + ", type=" + type + ", energyCompanyId="
               + energyCompanyId + ", entries=" + entries + "]";
    }
}
