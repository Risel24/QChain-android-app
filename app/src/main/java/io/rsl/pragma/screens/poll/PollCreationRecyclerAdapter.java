package io.rsl.pragma.screens.poll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rsl.pragma.R;
import io.rsl.pragma.screens.poll.models.Constants.ViewType;
import io.rsl.pragma.screens.poll.models.DatetimeModel;
import io.rsl.pragma.screens.poll.models.EditTextModel;
import io.rsl.pragma.screens.poll.models.TitleModel;
import io.rsl.pragma.screens.poll.models.TypeSelectorModel;
import io.rsl.pragma.screens.poll.models.core.CorePollModel;
import io.rsl.pragma.utils.BaseModel;
import io.rsl.pragma.utils.BaseViewHolder;
import io.rsl.pragma.utils.datetime.DateStringUtils;
import io.rsl.pragma.utils.datetime.DateTimeDialog;
import timber.log.Timber;

public class PollCreationRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<BaseModel> items;

    public PollCreationRecyclerAdapter(Context context, List<BaseModel> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewType.TITLE:
                return new TitleViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title, parent, false),
                        new RecyclerTextWatcher()
                );

            case ViewType.TYPE_SELECTOR:
                return new TypeViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_selector, parent, false),
                        new RecyclerTextWatcher()
                );

            case ViewType.VOTE_EDITTEXT:
                return new EditTextViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext, parent, false),
                        new RecyclerTextWatcher()
                );

            case ViewType.DATETIME:
                return new DatetimeViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_pickers, parent, false)
                );

            default:
                throw new IllegalArgumentException("Unknown view type!");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    /*
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(!payloads.isEmpty()) {
            if(payloads.get(0) instanceof Integer && holder instanceof RecyclerTextWatcherProvider) {
                ((RecyclerTextWatcherProvider) holder).getRecyclerTextWatcher().updatePosition((Integer) payloads.get(0));
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }*/

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ---------- potential bugs:

    private int getEditTextCount() {
        return getItemCount() - 3;
    }

    private TitleModel getTitleModel() {
        return (TitleModel) items.get(0);
    }

    private List<EditTextModel> getEditTextModels() {
        List<EditTextModel> editTextModels = new ArrayList<>();
        for(int i = 1; i < 1 + getEditTextCount(); i++) {
            editTextModels.add((EditTextModel) items.get(i));
        }
        return editTextModels;
    }

    private TypeSelectorModel getTypeSelectorModel() {
        return (TypeSelectorModel) items.get(getEditTextCount() + 1);
    }

    private DatetimeModel getDatetimeModel() {
        return (DatetimeModel) items.get(getEditTextCount() + 2);
    }

    // ----------------------------

    public CorePollModel getPoll() {
        List<String> voteOptions = new ArrayList<>();
        for(EditTextModel model : getEditTextModels()) {
            if(model.getVoteOption().length() > 0) {
                voteOptions.add(model.getVoteOption());
            }
        }

        TypeSelectorModel typeSelectorModel = getTypeSelectorModel();
        DatetimeModel datetimeModel = getDatetimeModel();

        return new CorePollModel(
                getTitleModel().getTitle(),
                voteOptions,
                typeSelectorModel.isMajority(),
                typeSelectorModel.getVoteCount(),
                datetimeModel.getStartDate(),
                datetimeModel.getEndDate()
        );
    }

    // ==========================================

    class EditTextViewHolder extends BaseViewHolder<EditTextModel> implements RecyclerTextWatcherProvider {

        @BindView(R.id.voteTextInput) EditText editText;
        @BindView(R.id.delete) ImageView deleteButton;
        RecyclerTextWatcher recyclerTextWatcher;

        EditTextViewHolder(View view, RecyclerTextWatcher recyclerTextWatcher) {
            super(view);
            ButterKnife.bind(this, view);
            this.recyclerTextWatcher = recyclerTextWatcher;
            editText.addTextChangedListener(recyclerTextWatcher);
        }

        @Override
        public void bind(EditTextModel editTextModel, final int position) {
            recyclerTextWatcher.updatePosition(position);
            editText.setText(editTextModel.getVoteOption());

            deleteButton.setOnClickListener(view -> {
                if(getEditTextCount() > 2) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount() - position);
                    /*for (int i = position; i < items.size(); i++) {
                        notifyItemChanged(i, i);
                    }*/
                } else {
                    Toast.makeText(context, "Can not delete last two options", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerTextWatcher getRecyclerTextWatcher() {
            return recyclerTextWatcher;
        }
    }

    // ==========================================

    class TitleViewHolder extends BaseViewHolder<TitleModel> implements RecyclerTextWatcherProvider {

        @BindView(R.id.titleInputedittext) EditText titleInput;
        RecyclerTextWatcher recyclerTextWatcher;

        TitleViewHolder(View view, RecyclerTextWatcher recyclerTextWatcher) {
            super(view);
            ButterKnife.bind(this, view);
            this.recyclerTextWatcher = recyclerTextWatcher;
            titleInput.addTextChangedListener(recyclerTextWatcher);
        }

        @Override
        public void bind(TitleModel titleModel, final int position) {
            recyclerTextWatcher.updatePosition(position);
            titleInput.setText(titleModel.getTitle());
        }

        @Override
        public RecyclerTextWatcher getRecyclerTextWatcher() {
            return recyclerTextWatcher;
        }
    }

    // ==========================================

    class TypeViewHolder extends BaseViewHolder<TypeSelectorModel> implements RecyclerTextWatcherProvider {

        @BindView(R.id.toggle) RadioGroup radioGroup;
        @BindView(R.id.voteCountInput) EditText countInput;
        RecyclerTextWatcher recyclerTextWatcher;

        TypeViewHolder(View view, RecyclerTextWatcher recyclerTextWatcher) {
            super(view);
            ButterKnife.bind(this, view);
            this.recyclerTextWatcher = recyclerTextWatcher;
            countInput.addTextChangedListener(recyclerTextWatcher);
        }

        @Override
        public void bind(final TypeSelectorModel typeSelectorModel, int position) {
            recyclerTextWatcher.updatePosition(position);
            countInput.setText(String.valueOf(typeSelectorModel.getVoteCount()));

            radioGroup.check(typeSelectorModel.isMajority() ? R.id.majority : R.id.cumulative);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.majority:
                        typeSelectorModel.setVoteCount(1);
                        countInput.setEnabled(false);
                        countInput.setText(String.valueOf(typeSelectorModel.getVoteCount()));
                        break;

                    case R.id.cumulative:
                        typeSelectorModel.setVoteCount(2);
                        countInput.setEnabled(true);
                        countInput.setText(String.valueOf(typeSelectorModel.getVoteCount()));
                        countInput.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
            });
        }

        @Override
        public RecyclerTextWatcher getRecyclerTextWatcher() {
            return recyclerTextWatcher;
        }
    }

    // ==========================================

    class DatetimeViewHolder extends BaseViewHolder<DatetimeModel> {

        @BindView(R.id.startDateButton) Button startDateButton;
        @BindView(R.id.endDateButton) Button endDateButton;

        DatetimeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bind(DatetimeModel datetimeModel, final int position) {
            Calendar now = Calendar.getInstance();
            startDateButton.setText(DateStringUtils.getFullDate(now));
            datetimeModel.setStartDate(now);

            endDateButton.setText("Manual stop");

            startDateButton.setOnClickListener(v -> {
                DateTimeDialog dateTimeDialog = new DateTimeDialog(context, DateTimeDialog.NOW);
                dateTimeDialog.showWithListener(calendar -> {
                    if(calendar == null) calendar = Calendar.getInstance();
                    ((Button) v).setText(DateStringUtils.getFullDate(calendar));
                    datetimeModel.setStartDate(calendar);
                });
            });

            endDateButton.setOnClickListener(v -> {
                DateTimeDialog dateTimeDialog = new DateTimeDialog(context, DateTimeDialog.MANUAL);
                dateTimeDialog.showWithListener(calendar -> {
                    if(calendar != null && calendar.compareTo(datetimeModel.getStartDate()) < 0) {
                        Toast.makeText(context, "End can not be before start", Toast.LENGTH_SHORT).show();
                        calendar = null;
                    }
                    ((Button) v).setText(calendar == null ? "Manual stop" : DateStringUtils.getFullDate(calendar));
                    datetimeModel.setEndDate(calendar);
                });
            });
        }
    }

    //===========================================

    class RecyclerTextWatcher implements TextWatcher {

        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (items.get(position).getViewType()) {
                case ViewType.TITLE:
                    ((TitleModel) items.get(position)).setTitle(s.toString());
                    break;
                case ViewType.TYPE_SELECTOR:
                    try {
                        ((TypeSelectorModel) items.get(position)).setVoteCount(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException ignored) {}
                    break;
                case ViewType.VOTE_EDITTEXT:
                    ((EditTextModel) items.get(position)).setVoteOption(s.toString());
                    if(!(items.get(position + 1) instanceof EditTextModel) && s.length() != 0 && getEditTextCount() <= 15) {
                        items.add(position + 1, new EditTextModel());
                        notifyItemInserted(position + 1);
                        notifyItemRangeChanged(position + 2, getItemCount() - position - 2);
                        /*for(int i = position + 2; i < items.size(); i++) {
                            notifyItemChanged(i, i);
                        }*/
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
