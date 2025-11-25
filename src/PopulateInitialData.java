package pr2_cuentas;

public final class PopulateInitialData {

    private PopulateInitialData() {
    }

    public static void cargar(Banco banco) {
        // MONEDAS
        // currentRate = valor de 1 unidad de esta moneda en ARS
        banco.crearCurrency(1, 1.0f,   "ARS", "Peso argentino");
        banco.crearCurrency(2, 1400f,  "USD", "Dólar estadounidense");
        banco.crearCurrency(3, 1600f,  "EUR", "Euro");
        banco.crearCurrency(4, 260f,   "BRL", "Real brasileño");


        // EMPLEADOS

        Usuario empleado1 = banco.crearUsuario(
                1000000, Usuario.TIPO_EMPLEADO,
                "Juan", "Pérez", "empleado1", "Cajero"
        );
        Usuario empleado2 = banco.crearUsuario(
                1000001, Usuario.TIPO_EMPLEADO,
                "Ana", "García", "empleado2", "Supervisor"
        );


        // CLIENTES PARTICULARES

        Usuario cliente1 = banco.crearUsuario(
                2000000, Usuario.TIPO_CLIENTE_PARTICULAR,
                "Carlos", "López", "cliente1", "Cliente particular"
        );
        Usuario cliente2 = banco.crearUsuario(
                20000001, Usuario.TIPO_CLIENTE_PARTICULAR,
                "María", "Rodríguez", "cliente2", "Cliente particular"
        );
        Usuario cliente3 = banco.crearUsuario(
                20000002, Usuario.TIPO_CLIENTE_PARTICULAR,
                "Lucía", "Sosa", "cliente3", "Cliente particular"
        );


        // CLIENTES INSTITUCIONES

        Usuario institucion1 = banco.crearUsuario(
                1000000111, Usuario.TIPO_CLIENTE_PARTICULAR,
                "Acme SA", "", "institucion1", "Institución"
        );
        Usuario institucion2 = banco.crearUsuario(
                1000000112, Usuario.TIPO_CLIENTE_PARTICULAR,
                "Servicios SRL", "", "institucion2", "Institución"
        );

        // CUENTAS

        // cliente1: ARS + USD
        Cuenta cuenta1_cliente1_ars = banco.crearCuenta(
                cliente1.getDni(), Cuenta.TIPO_CAJA_AHORRO,
                100_000f, "Caja ahorro ARS", 1);
        Cuenta cuenta2_cliente1_usd = banco.crearCuenta(
                cliente1.getDni(), Cuenta.TIPO_CUENTA_CORRIENTE,
                1_000f, "Cuenta corriente USD", 2);

        // cliente2: ARS + EUR
        Cuenta cuenta1_cliente2_ars = banco.crearCuenta(
                cliente2.getDni(), Cuenta.TIPO_CAJA_AHORRO,
                50_000f, "Caja ahorro ARS", 1);
        Cuenta cuenta2_cliente2_eur = banco.crearCuenta(
                cliente2.getDni(), Cuenta.TIPO_CAJA_AHORRO,
                500f, "Caja ahorro EUR", 3);

        // cliente3: solo BRL
        Cuenta cuenta1_cliente3_brl = banco.crearCuenta(
                cliente3.getDni(), Cuenta.TIPO_CAJA_AHORRO,
                5_000f, "Caja ahorro BRL", 4);

        // instituciones: ARS
        Cuenta cuenta1_institucion1_ars = banco.crearCuenta(
                institucion1.getDni(), Cuenta.TIPO_CUENTA_CORRIENTE,
                250_000f, "Cuenta empresa ARS", 1);
        Cuenta cuenta1_inst2_ars = banco.crearCuenta(
                institucion2.getDni(), Cuenta.TIPO_CUENTA_CORRIENTE,
                150_000f, "Cuenta empresa ARS", 1);


        // PRESTAMOS

        Prestamo prestamo1 = banco.crearPrestamo(
                cliente1.getDni(),
                Prestamo.TIPO_PRESTAMO_PARTICULAR,
                200_000f,
                365,
                0.50f,
                "Préstamo personal 12 meses 50% TNA",
                1   // ARS
        );

        Prestamo prestamo2 = banco.crearPrestamo(
                cliente2.getDni(),
                Prestamo.TIPO_PRESTAMO_ESPECIAL,
                50_000f,
                180,
                0.35f,
                "Préstamo especial 6 meses 35% TNA",
                1   // ARS
        );

        // Acreditar los préstamos en las cuentas ARS de cada cliente
        banco.depositar(cuenta1_cliente1_ars.getId(), 200_000f, "Acreditación préstamo #" + prestamo1.getId());
        banco.depositar(cuenta1_cliente2_ars.getId(), 50_000f, "Acreditación préstamo #" + prestamo2.getId());


        // INVERSIONES (DEPÓSITOS A PLAZO / SIMILAR)

        // Inversión ARS de cliente1
        Inversion inversion1 = banco.crearInversion(
                cliente1.getDni(),
                Inversion.TIPO_INVERSION_PARTICULAR,
                50_000f,
                180,
                0.40f,  // 40% TNA
                "Inversión 6 meses 40% TNA",
                1   // ARS
        );
        // retirar el capital invertido de su caja de ahorro ARS
        banco.retirar(cuenta1_cliente1_ars.getId(),
                50_000f,
                "Constitución inversión #" + inversion1.getId());

        // Inversión EUR de cliente2
        Inversion inversion2 = banco.crearInversion(
                cliente2.getDni(),
                Inversion.TIPO_INVERSION_ESPECIAL,
                300f,
                90,
                0.10f,  // 10% TNA
                "Inversión en EUR 90 días 10% TNA",
                3   // EUR
        );
        banco.retirar(cuenta2_cliente2_eur.getId(),
                300f,
                "Constitución inversión #" + inversion2.getId());


        // SERVICIOS

        // hacemos que los titulares sean las instituciones
        Servicio servMovil = banco.crearServicio(
                institucion1.getDni(),
                Servicio.TIPO_SERVICIO_RERCORRIDO,
                "Recarga móvil",
                "Operador móvil",
                1   // ARS
        );

        Servicio servLuz = banco.crearServicio(
                institucion2.getDni(),
                Servicio.TIPO_SERVICIO_RERCORRIDO,
                "Luz",
                "Empresa eléctrica",
                1   // ARS
        );

        Servicio servAgua = banco.crearServicio(
                institucion2.getDni(),
                Servicio.TIPO_SERVICIO_RERCORRIDO,
                "Agua",
                "Empresa de agua",
                1   // ARS
        );


        // TRANSACCIONES 

        // 1) Depósito adicional en cuenta ARS de cliente1
        banco.depositar(cuenta1_cliente1_ars.getId(), 10_000f, "Depósito en ventanilla");

        // 2) Retiro en cuenta ARS de cliente2
        banco.retirar(cuenta1_cliente2_ars.getId(), 5_000f, "Extracción cajero");

        // 3) Transferencia dentro de mismo cliente / misma moneda
        banco.transferir(cuenta1_cliente1_ars.getId(), cuenta1_institucion1_ars.getId(),
                15_000f, "Pago proveedor local");

        // 4) Transferencia entre diferentes monedas (USD -> BRL)
        banco.transferir(cuenta2_cliente1_usd.getId(), cuenta1_cliente3_brl.getId(),
                100f, "Transferencia internacional USD→BRL");

        // 5) Pago de servicio desde client1 ARS
        banco.pagarServicio(cuenta1_cliente1_ars.getId(), servMovil.getId(),
                2_000f, "Recarga celular");

        // 6) Pago de servicio (luz) desde client2 ARS
        banco.pagarServicio(cuenta1_cliente2_ars.getId(), servLuz.getId(),
                3_500f, "Pago factura luz");

        // 7) Transferencia empresa -> empresa
        banco.transferir(cuenta1_institucion1_ars.getId(), cuenta1_inst2_ars.getId(),
                20_000f, "Ajuste entre cuentas empresas");

        // 8) Pago de agua desde client3 BRL (con conversión)
        banco.pagarServicio(cuenta1_cliente3_brl.getId(), servAgua.getId(),
                500f, "Pago servicio de agua");
    }
}
