package com.ludwiglarsson.antiplanner.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.antiplanner.R
import kotlinx.coroutines.launch
import android.content.Context
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antiplanner.databinding.FragmentMainBinding
import com.ludwiglarsson.antiplanner.viewmodels.MainViewModel.Actions
import com.ludwiglarsson.antiplanner.viewmodels.MainViewModel.State
import com.google.android.material.snackbar.Snackbar
import com.ludwiglarsson.antiplanner.App
import com.ludwiglarsson.antiplanner.Callback
import com.ludwiglarsson.antiplanner.TodoAdapter
import com.ludwiglarsson.antiplanner.data.network.NetworkChangeListener
import com.ludwiglarsson.antiplanner.viewmodels.MainViewModel
import com.ludwiglarsson.antiplanner.viewmodels.appViewModels
import javax.inject.Inject

class MainFragment : Fragment(), Callback {

    @Inject
    lateinit var todoAdapter: TodoAdapter

    private val viewModel by appViewModels<MainViewModel>()
    private lateinit var binding: FragmentMainBinding
    private val networkListener = NetworkChangeListener { isConnected ->
        lifecycleScope.launch {
            viewModel.onOnlineChanged(isConnected)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val appComponent = (requireContext().applicationContext as App).appComponent
        val fragmentComponent = appComponent.getMainFragmentComponentFactory().create(this)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        NetworkChangeListener.register(requireContext(), networkListener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setUpCollects(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NetworkChangeListener.unregister(requireContext(), networkListener)
    }

    override fun onClickCheckBox(id: String, isDone: Boolean) {
        viewModel.onDoneClick(id, isDone)
    }

    override fun onClickText(id: String) {
        val editTodo = EditFragment.createNewInstance(id)
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, editTodo)
            .addToBackStack(null)
            .commit()
    }
    private fun setUpCollects(view: View) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.states.collect { state ->
                        binding.errorTodolist.root.isVisible = state is State.Error
                        binding.todolistLayout.root.isVisible = state is State.Success
                        binding.progressTodolist.root.isVisible = state is State.Loading
                        when (state) {
                            is State.Success -> showSuccessState(state)
                            is State.Loading -> showLoadingState()
                            State.Error -> showNoNetworkState()
                        }
                    }
                }
                launch {
                    viewModel.actions.collect { action ->
                        when (action) {
                            is Actions.Error -> showErrorAction(action, view)
                        }
                    }
                }
            }
        }
    }

    private fun showErrorAction(state: Actions.Error, view: View) {
        Snackbar.make(view, state.messageID, Snackbar.LENGTH_LONG).show()
    }

    private fun showLoadingState() {
        binding.networkError.visibility = View.GONE
    }

    private fun showNoNetworkState() {
        binding.networkError.visibility = View.GONE
    }

    private fun showSuccessState(state: State.Success) {
        if (!state.isOnline) {
            binding.networkError.visibility = View.VISIBLE
        } else {
            binding.networkError.visibility = View.GONE
        }
        todoAdapter.setListTodos(state.items)
        if (state.isHidden) {
            binding.todolistLayout.showHide.setImageResource(R.drawable.visible)
        } else {
            binding.todolistLayout.showHide.setImageResource(R.drawable.unvisible)
        }
        binding.todolistLayout.complete.text = getString(R.string.toolbar_subtitle, state.doneCount)
    }

    private fun setUpUI() {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.todolistLayout.recyclerView.adapter = todoAdapter
        binding.todolistLayout.recyclerView.layoutManager = layoutManager
        binding.todolistLayout.showHide.setOnClickListener {
            viewModel.onHideClick()
        }
        binding.errorTodolist.retryButton.setOnClickListener {
            viewModel.onRetryClick()
        }
    }
}