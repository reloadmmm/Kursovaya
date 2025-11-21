package com.example.kursovaya.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kursovaya.R;
import com.example.kursovaya.data.DiaryEntry;
import com.example.kursovaya.databinding.FragmentDiaryBinding;
import com.example.kursovaya.viewmodel.DiaryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class DiaryFragment extends Fragment {

    private FragmentDiaryBinding b;
    private DiaryAdapter adapter;
    private DiaryViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentDiaryBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(DiaryViewModel.class);

        b.rvDiary.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiaryAdapter();
        b.rvDiary.setAdapter(adapter);

        vm.getEntries().observe(getViewLifecycleOwner(), entries -> {
            adapter.submit(entries);
            b.tvDiaryEmpty.setVisibility(
                    (entries == null || entries.isEmpty()) ? View.VISIBLE : View.GONE
            );
        });

        b.fabDiary.setOnClickListener(v -> showAddDialog());

        attachSwipeToDelete();
    }

    // свайп влево → "Удалить"
    private void attachSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    private final Paint bgPaint = new Paint();
                    private final Paint textPaint = new Paint();

                    {
                        bgPaint.setColor(Color.parseColor("#F44336")); // красный фон
                        textPaint.setColor(Color.WHITE);
                        textPaint.setTextSize(40f);
                        textPaint.setAntiAlias(true);
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int pos = viewHolder.getBindingAdapterPosition();
                        DiaryEntry toDelete = adapter.getItem(pos);
                        if (toDelete == null) return;

                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Удалить запись?")
                                .setMessage("Это действие нельзя отменить.")
                                .setNegativeButton("Отмена", (d, w) -> {
                                    d.dismiss();
                                    adapter.notifyItemChanged(pos);
                                })
                                .setPositiveButton("Удалить",
                                        (d, w) -> vm.delete(toDelete))
                                .show();
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        if (dX < 0) {
                            float left = itemView.getRight() + dX;
                            float right = itemView.getRight();
                            float top = itemView.getTop();
                            float bottom = itemView.getBottom();

                            c.drawRect(left, top, right, bottom, bgPaint);

                            String text = "Удалить";
                            float textWidth = textPaint.measureText(text);
                            float textX = right - textWidth - 40;
                            float textY = top + (bottom - top) / 2f
                                    - (textPaint.descent() + textPaint.ascent()) / 2f;
                            c.drawText(text, textX, textY, textPaint);
                        }

                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                                actionState, isCurrentlyActive);
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(b.rvDiary);
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_diary_entry, null, false);

        TextInputEditText etText = dialogView.findViewById(R.id.etDiaryText);
        ImageButton btnRecord = dialogView.findViewById(R.id.btnRecord);
        ImageButton btnPlay = dialogView.findViewById(R.id.btnPlay);
        View wave1 = dialogView.findViewById(R.id.wave1);
        View wave2 = dialogView.findViewById(R.id.wave2);
        View wave3 = dialogView.findViewById(R.id.wave3);
        TextView tvStatus = dialogView.findViewById(R.id.tvRecordStatus);

        // iOS-иконки по умолчанию
        btnRecord.setImageResource(R.drawable.ic_mic_ios);
        btnPlay.setImageResource(R.drawable.ic_play_ios);
        btnPlay.setVisibility(View.GONE);
        tvStatus.setText("Нажми и запиши голосовую заметку");

        final MediaRecorder[] recorder = new MediaRecorder[1];
        final MediaPlayer[] player = new MediaPlayer[1];
        final boolean[] isRecording = {false};
        final boolean[] isPlaying = {false};
        final String[] audioPath = {null};

        AnimatorSet waveAnim = createWaveAnimator(wave1, wave2, wave3);

        // запись (iOS-микрофон + анимации)
        btnRecord.setOnClickListener(v -> {
            if (!isRecording[0]) {
                // анимация «нажатия» как на iOS
                btnRecord.animate()
                        .scaleX(1.12f)
                        .scaleY(1.12f)
                        .setDuration(160)
                        .start();

                try {
                    File dir = requireContext().getExternalFilesDir("diary_audio");
                    if (dir != null && !dir.exists()) dir.mkdirs();
                    String fileName = "entry_" + System.currentTimeMillis() + ".m4a";
                    File out = new File(dir, fileName);

                    MediaRecorder r = new MediaRecorder();
                    r.setAudioSource(MediaRecorder.AudioSource.MIC);
                    r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    r.setAudioEncodingBitRate(128000);
                    r.setAudioSamplingRate(44100);
                    r.setOutputFile(out.getAbsolutePath());
                    r.prepare();
                    r.start();

                    recorder[0] = r;
                    audioPath[0] = out.getAbsolutePath();
                    isRecording[0] = true;

                    tvStatus.setText("Запись… говори!");
                    wave1.setVisibility(View.VISIBLE);
                    wave2.setVisibility(View.VISIBLE);
                    wave3.setVisibility(View.VISIBLE);
                    waveAnim.start();

                } catch (IOException e) {
                    Toast.makeText(getContext(),
                            "Не удалось начать запись", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    btnRecord.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
                }
            } else {
                // остановить запись
                try {
                    if (recorder[0] != null) {
                        recorder[0].stop();
                        recorder[0].release();
                        recorder[0] = null;
                    }
                } catch (Exception ignored) { }

                isRecording[0] = false;
                btnRecord.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
                waveAnim.cancel();
                resetWaveViews(wave1, wave2, wave3);

                tvStatus.setText("Запись сохранена. Можно прослушать.");
                btnPlay.setVisibility(View.VISIBLE);
            }
        });

        // предпрослушка (play/pause)
        btnPlay.setOnClickListener(v -> {
            if (audioPath[0] == null) return;

            if (!isPlaying[0]) {
                try {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(audioPath[0]);
                    mp.prepare();
                    mp.start();
                    player[0] = mp;
                    isPlaying[0] = true;

                    btnPlay.setImageResource(R.drawable.ic_pause_ios);
                    tvStatus.setText("Воспроизведение…");

                    mp.setOnCompletionListener(m -> {
                        isPlaying[0] = false;
                        btnPlay.setImageResource(R.drawable.ic_play_ios);
                        tvStatus.setText("Прослушивание завершено.");
                        m.release();
                        player[0] = null;
                    });
                } catch (IOException e) {
                    Toast.makeText(getContext(),
                            "Ошибка воспроизведения", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                if (player[0] != null) {
                    player[0].stop();
                    player[0].release();
                    player[0] = null;
                }
                isPlaying[0] = false;
                btnPlay.setImageResource(R.drawable.ic_play_ios);
                tvStatus.setText("Остановлено.");
            }
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Отмена", (d, w) -> {
                    d.dismiss();
                    // чистим ресурсы
                    if (recorder[0] != null) {
                        try { recorder[0].stop(); } catch (Exception ignored) {}
                        recorder[0].release();
                    }
                    if (player[0] != null) {
                        player[0].stop();
                        player[0].release();
                    }
                })
                .setPositiveButton("Сохранить", (d, w) -> {
                    String text = etText.getText() == null
                            ? "" : etText.getText().toString().trim();
                    if (text.isEmpty() && audioPath[0] == null) {
                        Toast.makeText(getContext(),
                                "Пустую запись сохранить нельзя", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    vm.add(text, audioPath[0]);
                    if (player[0] != null) {
                        player[0].stop();
                        player[0].release();
                    }
                })
                .show();
    }

    private AnimatorSet createWaveAnimator(View w1, View w2, View w3) {
        // чуть «айосовый» мягкий waveform – разные фазы и амплитуды
        ObjectAnimator a1 = ObjectAnimator.ofFloat(w1, "scaleY", 0.4f, 1.2f);
        a1.setRepeatCount(ValueAnimator.INFINITE);
        a1.setRepeatMode(ValueAnimator.REVERSE);
        a1.setDuration(360);

        ObjectAnimator a2 = ObjectAnimator.ofFloat(w2, "scaleY", 0.3f, 1.4f);
        a2.setRepeatCount(ValueAnimator.INFINITE);
        a2.setRepeatMode(ValueAnimator.REVERSE);
        a2.setDuration(430);

        ObjectAnimator a3 = ObjectAnimator.ofFloat(w3, "scaleY", 0.5f, 1.1f);
        a3.setRepeatCount(ValueAnimator.INFINITE);
        a3.setRepeatMode(ValueAnimator.REVERSE);
        a3.setDuration(390);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(a1, a2, a3);
        return set;
    }

    private void resetWaveViews(View w1, View w2, View w3) {
        w1.setScaleY(0.4f);
        w2.setScaleY(0.3f);
        w3.setScaleY(0.5f);
    }
}
