package com.example.employeeapp2

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.progressBar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_link.setOnClickListener {
            findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }

        register.setOnClickListener {
            email_layout_register.error = null
            password_layout_register.error = null

            val email = email_register.text.toString()
            val pass = password_register.text.toString()
            val cpass = confirm_password_register.text.toString()

            if(validateInput(email, pass, cpass)) {
                progressBar.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity()) { task ->
                        progressBar.visibility = View.INVISIBLE
                        if(task.isSuccessful) {
                            findNavController().navigate(
                                RegisterFragmentDirections.actionRegisterFragmentToEmployeeListFragment()
                            )
                        } else {
                            val toast = Toast.makeText(requireActivity(),
                                "Authentication Failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                            toast.show()
                        }
                    }
            }
        }

    }

    private fun validateInput(email: String, pass: String, cpass: String): Boolean {
        var valid = true
        if(email.isBlank()) {
            email_layout_register.error = "Please enter an email address"
            valid = false
        }
        if(pass.isBlank()) {
            password_layout_register.error = "Please enter password"
            valid = false
        } else if(pass.length < 8) {
            password_layout_register.error = "Password should be 8 characters or more"
            valid = false
        } else if(pass != cpass) {
            confirm_password_layout_register.error = "Password mismatch"
            valid = false
        }

        return valid
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}