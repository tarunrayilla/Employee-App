package com.example.employeeapp2

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_employee_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

const val READ_FILE_REQUEST = 1
const val CREATE_FILE_REQUEST = 2



/**
 * A simple [Fragment] subclass.
 * Use the [EmployeeListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmployeeListFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: EmployeeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(this).get(EmployeeListViewModel::class.java)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        toolbar_show.inflateMenu(R.menu.list_menu)
//        toolbar_show.setOnMenuItemClickListener {
//            handleMenuItem(it)
//        }


        setUpNavigationDrawer()

        with(employee_list) {
            layoutManager = LinearLayoutManager(activity)
            adapter = EmployeeAdapter{show, id ->
                if(show) {
                    findNavController().navigate(
                        EmployeeListFragmentDirections.actionEmployeeListFragmentToShowEmployeeFragment(id)
                    )
                } else {
                    findNavController().navigate(
                        EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(id)
                    )
                }

            }
        }

        add_employee.setOnClickListener {
            findNavController().navigate(
                EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0)
            )
        }

        viewModel.employees.observe(viewLifecycleOwner, Observer{
            (employee_list.adapter as EmployeeAdapter).submitList(it)
            if(it.isNotEmpty()) {
                no_employee_record.visibility = View.INVISIBLE
            } else {
                no_employee_record.visibility = View.VISIBLE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_import_data -> {
//                importEmployees()
//                true
//            }
//            R.id.menu_export_data -> {
//                GlobalScope.launch {
//                    exportEmployees()
//                }
//                true
//            }
//            R.id.menu_latest_employee_name -> {
//                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
//                val name = sharedPref.getString(LATEST_EMPLOYEE_NAME_KEY, "")
//                if(!name.isNullOrEmpty()) {
//                    Toast.makeText(requireActivity(), "The name of latest employee is ${name}", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(requireActivity(), "No employee added yet", Toast.LENGTH_SHORT).show()
//                }
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    private fun handleMenuItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_import_data -> {
                importEmployees()
                true
            }
            R.id.menu_export_data -> {
                GlobalScope.launch {
                    exportEmployees()
                }
                true
            }
            R.id.menu_latest_employee_name -> {
                val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                val name = sharedPref.getString(LATEST_EMPLOYEE_NAME_KEY, "")
                if(!name.isNullOrEmpty()) {
                    Toast.makeText(requireActivity(), "The name of latest employee is ${name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), "No employee added yet", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpNavigationDrawer() {
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)


        //NavigationUI.setupWithNavController(toolbar_show, navController, drawerLayout)
        NavigationUI.setupWithNavController(
            toolbar_show, navController,
            AppBarConfiguration.Builder(R.id.navigation, R.id.employeeListFragment)
                .setDrawerLayout(drawerLayout)
                .build()
        )

        navigationView.setupWithNavController(navController)

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            when (it.itemId) {
                R.id.add_new ->  findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(
                        0
                    )
                )
                R.id.contact ->  findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToContactFragment()
                )
                R.id.about ->  findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToAboutFragment()
                )
                R.id.menu_import_data -> {
                    importEmployees()
                }
                R.id.menu_export_data -> {
                    GlobalScope.launch {
                        exportEmployees()
                    }
                }
                R.id.menu_latest_employee_name -> {
                    val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
                    val name = sharedPref.getString(LATEST_EMPLOYEE_NAME_KEY, "")
                    if(!name.isNullOrEmpty()) {
                        Toast.makeText(requireActivity(), "The name of latest employee is ${name}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireActivity(), "No employee added yet", Toast.LENGTH_SHORT).show()
                    }
                }
                R.id.menu_alarm -> {
                    val alarmMgr = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
                        PendingIntent.getBroadcast(context, 0, intent, 0)
                    }

                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                            //API >= 23
                            alarmMgr.setExactAndAllowWhileIdle(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + 30 * 60 * 1000,
                                alarmIntent)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
                            //API >= 19
                            alarmMgr.setExact(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + 30 * 60 * 1000,
                                alarmIntent)
                        }
                        else -> {
                            alarmMgr.set(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + 30 * 60 * 1000,
                                alarmIntent)
                        }
                    }
                    Toast.makeText(requireActivity(), getString(R.string.alarm_set_message), Toast.LENGTH_SHORT).show()
                }

                R.id.company_chat -> {
                    findNavController().navigate(
                        EmployeeListFragmentDirections.actionEmployeeListFragmentToChatFragment()
                    )
                }

                R.id.sign_out -> {
                    auth.signOut()
                    auth.addAuthStateListener {
                        if(auth.currentUser == null) {
                            //listener is called multiple times so check if we are in correct state
                            val currId = findNavController().currentDestination!!.id
                            if(currId == R.id.employeeListFragment) {
                                findNavController().navigate(
                                    EmployeeListFragmentDirections.actionEmployeeListFragmentToLoginFragment()
                                )
                            }
                        }
                    }

                }
                else -> super.onOptionsItemSelected(it)
            }
            true

        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK) {
//            when (requestCode) {
//                READ_FILE_REQUEST -> {
//                    GlobalScope.launch{
//                        data?.data?.also { uri ->
//                            readFromFile(uri)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun importEmployees(){
//        Intent(Intent.ACTION_GET_CONTENT).also { readFileIntent ->
//            readFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
//            readFileIntent.type = "text/*"
//            readFileIntent.resolveActivity(requireActivity().packageManager)?.also {
//                startActivityForResult(readFileIntent, READ_FILE_REQUEST)
//            }
//        }
//    }
//
//    private suspend fun readFromFile(uri: Uri){
//        try {
//            requireActivity().contentResolver.openFileDescriptor(uri, "r")?.use {
//                withContext(Dispatchers.IO) {
//                    FileInputStream(it.fileDescriptor).use {
//                        parseCSVFile(it)
//                    }
//                }
//            }
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    private suspend fun parseCSVFile(stream: InputStream){
//        val employees = mutableListOf<Employee>()
//
//        BufferedReader(InputStreamReader(stream)).forEachLine {
//            val tokens = it.split(",")
//            employees.add(Employee(id = 0, name = tokens[0], role = tokens[1],
//                age = tokens[2].toInt(), gender = tokens[3].toInt(), photo = ""))
//        }
//
//        if(employees.isNotEmpty()){
//            viewModel.insertEmployees(employees)
//        }
//    }
//
//    private suspend fun exportEmployees(){
//        var csvFile: File? = null
//        withContext(Dispatchers.IO) {
//            csvFile = try {
//                createFile(requireActivity(),"Documents", "csv")
//            } catch (ex: IOException) {
//                Toast.makeText(requireActivity(), getString(R.string.file_create_error, ex.message),
//                    Toast.LENGTH_SHORT). show()
//                null
//            }
//
//            csvFile?.printWriter()?.use { out ->
//                val employees = viewModel.getEmployeeList()
//                if(employees.isNotEmpty()){
//                    employees.forEach{
//                        out.println(it.name + "," + it.role + "," + it.age + "," + it.gender)
//                    }
//                }
//            }
//        }
//        withContext(Dispatchers.Main){
//            csvFile?.let{
//                val uri = FileProvider.getUriForFile(
//                    requireActivity(), BuildConfig.APPLICATION_ID + ".fileprovider",
//                    it)
//                launchFile(uri, "csv")
//            }
//        }
//    }
//
//    private fun launchFile(uri: Uri, ext: String){
//        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        intent.setDataAndType(uri, mimeType)
//        if(intent.resolveActivity(requireActivity().packageManager) != null){
//            startActivity(intent)
//        } else{
//            Toast.makeText(requireActivity(), getString(R.string.no_app_csv), Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                READ_FILE_REQUEST -> {
                    data?.data?.also { uri ->
                        GlobalScope.launch {
                            readFromFile(uri)
                        }
                    }
                }
                CREATE_FILE_REQUEST -> {
                    data?.data?.also { uri ->
                        GlobalScope.launch {
                            if(writeToFile(uri)){
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireActivity(), getString(R.string.file_export_success),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun exportEmployees(){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
            putExtra(Intent.EXTRA_TITLE, "employee_list.csv")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST)
    }

    private suspend fun writeToFile(uri: Uri): Boolean{
        try {
            requireActivity().contentResolver.openFileDescriptor(uri, "w")?.use {pfd ->
                FileOutputStream(pfd.fileDescriptor).use {outStream ->
                    val employees = viewModel.getEmployeeList()
                    if(employees.isNotEmpty()){
                        employees.forEach{
                            outStream.write((it.name + "," + it.role + "," + it.age + "," + it.gender + "\n").toByteArray())
                        }
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun importEmployees(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"

        }
        intent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(intent, READ_FILE_REQUEST)
        }
    }

    private suspend fun readFromFile(uri: Uri){

        try {
            requireActivity().contentResolver.openFileDescriptor(uri, "r")?.use {
                FileInputStream(it.fileDescriptor).use {
                    withContext(Dispatchers.IO) {
                        parseCSVFile(it)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private suspend fun parseCSVFile(stream: FileInputStream){
        val employees = mutableListOf<Employee>()
        BufferedReader(InputStreamReader(stream)).forEachLine {
            val tokens = it.split(",")
            employees.add(Employee(id = 0, name = tokens[0], role = tokens[1],
                age = tokens[2].toInt(), gender = tokens[3].toInt(), photo = ""))
        }

        if(employees.isNotEmpty()){
            viewModel.insertEmployees(employees)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Toast.makeText(requireActivity(), "Portrait",Toast.LENGTH_SHORT).show()
            employee_list.layoutManager = LinearLayoutManager(activity)
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(requireActivity(), "Landscape", Toast.LENGTH_SHORT).show()
            with(employee_list) {
                layoutManager = GridLayoutManager(activity, 2)

            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EmployeeListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmployeeListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}