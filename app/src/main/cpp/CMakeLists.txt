cmake_minimum_required(VERSION 3.4.1)

# Tạo thư viện shared (.so) từ file native-lib.cpp
add_library(
        matrix          # Tên thư viện => sẽ load bằng System.loadLibrary("native-lib")
        SHARED
        matrix.cpp      # Các file nguồn native
)

# Tìm thư viện log của Android (nằm sẵn trong hệ thống)
find_library(
        log-lib
        log
#        /system/vendor/lib/libOpenCL.so
)

# Liên kết thư viện native với thư viện log
target_link_libraries(
        matrix
        ${log-lib}
        libOpenCL.so
)